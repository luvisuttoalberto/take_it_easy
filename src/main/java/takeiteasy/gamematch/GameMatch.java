package takeiteasy.gamematch;

import takeiteasy.board.*;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;

import java.util.Vector;

public class GameMatch implements IGameMatch{

    Vector<IPlayer> players = new Vector<IPlayer>();
    TilePool tilePool;
    State state;
    Integer currentTileIndex;

    public GameMatch() {
        tilePool = new TilePool();
        state = State.SETUP;
        currentTileIndex = 0;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setTilePoolSeed(long seed){
        tilePool.reset(seed);
    }

    @Override
    public String[] getPlayerNames() {

        String[] playerNames = players.stream().map(IPlayer::getName).toArray(String[]::new);
        return playerNames;
    }

    private Integer retrievePlayerIndexFromName(String playerName) throws PlayerNameNotFoundException {

        for (Integer i = 0; i< players.size();++i){
            if (players.get(i).getName() == playerName){
                return i;
            }
        }
        throw new PlayerNameNotFoundException(playerName);
    }

    @Override
    public IBoard getBoardFromPlayerName(String playerName) throws PlayerNameNotFoundException {
        return players.get(retrievePlayerIndexFromName(playerName)).getBoard();
    }

    @Override
    public void addPlayer(IPlayer player) throws PlayerWithSameNameCannotBeAddedException {
        try{
            retrievePlayerIndexFromName(player.getName());
            throw new PlayerWithSameNameCannotBeAddedException(player.getName());
        }
        catch (PlayerNameNotFoundException e){
            players.add(player);
        }
    }

    @Override
    public void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException {
        Integer playerIndex = retrievePlayerIndexFromName(oldName);
        players.get(playerIndex).setName(newName);
    }

    @Override
    public void removePlayer(String playerName) throws PlayerNameNotFoundException {
        Integer playerIndex = retrievePlayerIndexFromName(playerName);
        players.removeElementAt(playerIndex);
    }

    @Override
    public void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException, OutOfProperStateException {

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

    @Override
    public Integer getCurrentTileIndex() {
        return currentTileIndex;
    }

    @Override
    public Tile getCurrentTile() {
        return tilePool.getTile(currentTileIndex);
    }

    @Override
    public void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, OutOfProperStateException {
        players.get(retrievePlayerIndexFromName(playerName)).placeTile(getCurrentTile(), coordinates);
    }

    @Override
    public void dealNextTile() throws InvalidMatchStateException, NotEnoughPlayersException, PlayerNotReadyForNextTile, TilePoolDepletedException {
        if (state != State.PLAY){
            throw new InvalidMatchStateException();
        }
        if (players.size()<1){
            throw new NotEnoughPlayersException();
        }
        for (IPlayer p : players){
            IPlayer.State pstate = p.getState();
            if (pstate== IPlayer.State.PLACING){
                throw new PlayerNotReadyForNextTile();
            }
        }
        if (currentTileIndex >= tilePool.getSize()-1){
            throw new TilePoolDepletedException();
        }

        // update currentTileIndex and player states
        ++currentTileIndex;
        for (IPlayer p : players){
            try{
                p.transitionFromWaitingPlayersToPlacing();
            }
            catch (OutOfProperStateException ignored){
            }
        }
    }

    @Override
    public void abortMatch() {

    }

    @Override
    public void endMatch() {

    }
}
