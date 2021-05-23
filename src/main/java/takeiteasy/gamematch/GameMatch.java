package takeiteasy.gamematch;

import org.json.JSONArray;
import org.json.JSONObject;
import takeiteasy.JSONKeys;
import takeiteasy.board.*;
import takeiteasy.board.exceptions.BadHexCoordinatesException;
import takeiteasy.board.exceptions.CoordinatesOccupidedException;
import takeiteasy.board.exceptions.OutOfBoardCoordinatesException;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;

import java.util.Vector;

public class GameMatch implements IGameMatch{

    Vector<IPlayer> players = new Vector<>();
    TilePool tilePool;
    State state;
    Integer currentTileIndex;

    public GameMatch() {
        tilePool = new TilePool();
        state = State.SETUP;
        currentTileIndex = 0;
    }

    @Override
    public void setTilePoolSeed(long seed) throws InvalidMatchStateException {
        if(state != State.SETUP) {
            throw new InvalidMatchStateException();
        }
        tilePool.reset(seed);
    }

    private Integer retrievePlayerIndexFromName(String playerName) throws PlayerNameNotFoundException {

        for (int i = 0; i < players.size(); ++i){
            if (players.get(i).getName().equals(playerName)){
                return i;
            }
        }
        throw new PlayerNameNotFoundException(playerName);
    }

    @Override
    public void addPlayer(String playerName) throws PlayersWithSameNameNotAllowedException, InvalidMatchStateException {
        if(state != State.SETUP){
            throw new InvalidMatchStateException();
        }
        try{
            retrievePlayerIndexFromName(playerName);
            throw new PlayersWithSameNameNotAllowedException(playerName);
        }
        catch (PlayerNameNotFoundException e){
            players.add(new Player(playerName));
        }
    }

    @Override
    public void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException, InvalidMatchStateException, PlayersWithSameNameNotAllowedException {
        if(state != State.SETUP){
            throw new InvalidMatchStateException();
        }
        Integer playerIndex = retrievePlayerIndexFromName(oldName);
        try {
            retrievePlayerIndexFromName(newName);
            throw new PlayersWithSameNameNotAllowedException(newName);
        } catch (PlayerNameNotFoundException ignored){
            players.get(playerIndex).setName(newName);
        }
    }

    @Override
    public void removePlayer(String playerName) throws PlayerNameNotFoundException, NotEnoughPlayersException, LastPlacingPlayerRemovedException {
        Integer playerIndex = retrievePlayerIndexFromName(playerName);
        if(players.size() <= 1){
            throw new NotEnoughPlayersException();
        }
        players.removeElementAt(playerIndex);
        for(IPlayer player : players){
            if(player.getState() != IPlayer.State.WAIT_OTHER){
                return;
            }
        }

        throw new LastPlacingPlayerRemovedException();
    }

    @Override
    public void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException, InvalidPlayerStateException {

        if(state != State.SETUP){
            throw new InvalidMatchStateException();
        }
        if(players.size() < 1){
            throw new NotEnoughPlayersException();
        }
        for(IPlayer p : players){
            p.startMatch();
        }
        state = State.PLAY;
    }

    private Tile getCurrentTile() {
        return tilePool.getTile(currentTileIndex);
    }

    @Override
    public void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException {
        players.get(retrievePlayerIndexFromName(playerName)).placeTile(getCurrentTile(), coordinates);
    }

    @Override
    public void dealNextTile() throws InvalidMatchStateException, NotEnoughPlayersException, PlayersNotReadyForNextTileException, TilePoolDepletedException {
        if (state != State.PLAY){
            throw new InvalidMatchStateException();
        }
        //TODO: This part of the code is never reached!!!
//        if (players.size() < 1){
//            throw new NotEnoughPlayersException();
//        }
        for (IPlayer p : players){
            IPlayer.State playerState = p.getState();
            if (playerState == IPlayer.State.PLACING){
                throw new PlayersNotReadyForNextTileException();
            }
        }
        if (currentTileIndex >= tilePool.getSize() - 1){
            throw new TilePoolDepletedException();
        }

        // update currentTileIndex and player states
        ++currentTileIndex;
        for (IPlayer p : players){
            try{
                p.transitionFromWaitingPlayersToPlacing();
            }
            catch (InvalidPlayerStateException ignored){
            }
        }
    }

    @Override
    public void backToSetup() throws InvalidMatchStateException {
        if(state == State.SETUP){
            throw new InvalidMatchStateException();
        }
        currentTileIndex = 0;
        for(IPlayer p : players){
            p.reset();
        }
        state = State.SETUP;
    }

    @Override
    public void endMatch() throws InvalidMatchStateException, TilePoolNotDepletedException, PlayersNotReadyToEndMatchException, InvalidPlayerStateException {

        if(state != State.PLAY){
            throw new InvalidMatchStateException();
        }

        if(currentTileIndex < tilePool.getSize() - 1){
            throw new TilePoolNotDepletedException();
        }

        for(IPlayer p : players){
            if(p.getState() != IPlayer.State.WAIT_OTHER){
                throw new PlayersNotReadyToEndMatchException();
            }
        }

        for(IPlayer p : players){
            p.endMatch();
        }

        state = State.FINISH;
    }

    @Override
    public JSONObject getData() {
        JSONObject matchData = new JSONObject();
        JSONArray playersData = new JSONArray();
        for(IPlayer p : players){
            playersData.put(p.getData());
        }
        matchData.put(JSONKeys.MATCH_PLAYERS, playersData);
        matchData.put(JSONKeys.MATCH_CURRENT_TILE, tilePool.getTile(currentTileIndex).getData());
        matchData.put(JSONKeys.MATCH_SEED, tilePool.getSeed());
        matchData.put(JSONKeys.MATCH_STATE, state.name());

        return matchData;
    }
}
