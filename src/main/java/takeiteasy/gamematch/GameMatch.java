package takeiteasy.gamematch;

import org.json.JSONArray;
import org.json.JSONObject;
import takeiteasy.JSONKeys;
import takeiteasy.board.*;
import takeiteasy.board.exceptions.*;
import takeiteasy.gamematch.exceptions.*;
import takeiteasy.player.*;
import takeiteasy.player.exceptions.InvalidPlayerStateException;
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

    private Boolean areAllPlayersInState(IPlayer.State state){
        return players.stream().allMatch(p -> p.getState() == state);
    }

    //TODO: maybe this can be substituted by the AND of
    //      areAllPlayersInState(WAIT_OTHER) AND areAllPlayersInState(WAIT_MATCH)
    private Boolean isThereAPlayerInState(IPlayer.State state){
        return players.stream().anyMatch(p -> p.getState() == state);
    }

    private Boolean isTilePoolDepleted(){
        return currentTileIndex >= tilePool.getSize() - 1;
    }

    @Override
    public void setTilePoolSeed(long seed) throws InvalidMatchStateException {
        if(state != State.SETUP) {
            throw new InvalidMatchStateException();
        }
        tilePool.reset(seed);
    }

    Boolean isThereAPlayerNamed(String playerName){
        return players.stream().anyMatch(p -> p.getName().equals(playerName));
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

        if(isThereAPlayerNamed(playerName)){
            throw new PlayersWithSameNameNotAllowedException(playerName);
        }
        else{
            players.add(new Player(playerName));
        }
    }

    @Override
    public void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException, InvalidMatchStateException, PlayersWithSameNameNotAllowedException {

        if(state != State.SETUP){
            throw new InvalidMatchStateException();
        }

        //TODO: this throws an exception if oldName isn't found; should we do this explicitly?
        Integer playerIndex = retrievePlayerIndexFromName(oldName);

        if(isThereAPlayerNamed(newName)){
            throw new PlayersWithSameNameNotAllowedException(newName);
        }
        else{
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

        if(areAllPlayersInState(IPlayer.State.WAIT_OTHER)){
            throw new LastPlacingPlayerRemovedException();
        }

    }

    @Override
    public void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException {

        if(state != State.SETUP){
            throw new InvalidMatchStateException();
        }
        if(players.size() < 1){
            throw new NotEnoughPlayersException();
        }
        players.forEach(p -> {
            try{p.startMatch();}
            catch (InvalidPlayerStateException ignored){}
        });
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
    public void dealNextTile() throws InvalidMatchStateException, PlayersNotReadyForNextTileException, TilePoolDepletedException {
        if (state != State.PLAY){
            throw new InvalidMatchStateException();
        }
        if(isThereAPlayerInState(IPlayer.State.PLACING)){
            throw new PlayersNotReadyForNextTileException();
        }
        if (isTilePoolDepleted()){
            throw new TilePoolDepletedException();
        }

        ++currentTileIndex;
        players.forEach(p -> {
            try {p.transitionFromWaitingPlayersToPlacing();}
            catch (InvalidPlayerStateException ignored) {}
        });
    }

    @Override
    public void backToSetup() throws InvalidMatchStateException {
        if(state == State.SETUP){
            throw new InvalidMatchStateException();
        }
        
        currentTileIndex = 0;

        //TODO: should this be converted as intellij suggests?
        players.forEach(p -> p.reset());

        state = State.SETUP;
    }

    @Override
    public void endMatch() throws InvalidMatchStateException, TilePoolNotDepletedException, PlayersNotReadyToEndMatchException {

        if(state != State.PLAY){
            throw new InvalidMatchStateException();
        }

        if(!isTilePoolDepleted()){
            throw new TilePoolNotDepletedException();
        }

        if(!areAllPlayersInState(IPlayer.State.WAIT_OTHER)){
            throw new PlayersNotReadyToEndMatchException();
        }

        players.forEach(p -> {
            try{p.endMatch();}
            catch (InvalidPlayerStateException ignored){}
        });

        state = State.FINISH;
    }

    @Override
    public JSONObject getData() {
        JSONObject matchData = new JSONObject();
        JSONArray playersData = new JSONArray();
        players.forEach(p -> playersData.put(p.getData()));

        matchData.put(JSONKeys.MATCH_PLAYERS, playersData);
        matchData.put(JSONKeys.MATCH_CURRENT_TILE, tilePool.getTile(currentTileIndex).getData());
        matchData.put(JSONKeys.MATCH_SEED, tilePool.getSeed());
        matchData.put(JSONKeys.MATCH_STATE, state.name());

        return matchData;
    }
}
