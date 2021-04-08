package takeiteasy.gamematch;

import org.json.JSONObject;
import takeiteasy.board.*;
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

//    @Override
//    public State getState() {
//        return state;
//    }

    @Override
    public void setTilePoolSeed(long seed) throws InvalidMatchStateException {
        if(state != State.SETUP) {
            throw new InvalidMatchStateException();
        }
        tilePool.reset(seed);
    }

//    @Override
//    public String[] getPlayerNames() {
//        return players.stream().map(IPlayer::getName).toArray(String[]::new);
//    }

    private Integer retrievePlayerIndexFromName(String playerName) throws PlayerNameNotFoundException {

        for (int i = 0; i < players.size(); ++i){
            if (players.get(i).getName().equals(playerName)){
                return i;
            }
        }
        throw new PlayerNameNotFoundException(playerName);
    }

//    @Override
//    public IBoard getBoardFromPlayerName(String playerName) throws PlayerNameNotFoundException {
//        return players.get(retrievePlayerIndexFromName(playerName)).getBoard();
//    }

//    @Override
//    public IPlayer.State getPlayerStateFromPlayerName(String playerName) throws PlayerNameNotFoundException {
//        return players.get(retrievePlayerIndexFromName(playerName)).getState();
//    }

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
    public void removePlayer(String playerName) throws PlayerNameNotFoundException, NotEnoughPlayersException {
        Integer playerIndex = retrievePlayerIndexFromName(playerName);
        if(players.size() <= 1){
            throw new NotEnoughPlayersException();
        }
        players.removeElementAt(playerIndex);
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

//    @Override
//    public Integer getCurrentTileIndex() {
//        return currentTileIndex;
//    }

    //TODO: Privatize it and correct the Test !!!
    @Override
    public Tile getCurrentTile() {
        return tilePool.getTile(currentTileIndex);
    }

//    @Override
//    public long getSeed() {
//        return tilePool.getSeed();
//    }

    @Override
    public void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException {
        players.get(retrievePlayerIndexFromName(playerName)).placeTile(getCurrentTile(), coordinates);
    }

    @Override
    public void dealNextTile() throws InvalidMatchStateException, NotEnoughPlayersException, PlayersNotReadyForNextTileException, TilePoolDepletedException {
        if (state != State.PLAY){
            throw new InvalidMatchStateException();
        }
        if (players.size() < 1){
            throw new NotEnoughPlayersException();
        }
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
        JSONObject data = new JSONObject();
        JSONObject playersData = new JSONObject();
        for(IPlayer p : players){
            playersData.put(p.getName(), p.getData());
        }
        data.put("players", playersData);

        data.put("currentTile", tilePool.getTile(currentTileIndex).getData());

        data.put("seed", tilePool.getSeed());

        data.put("matchState", state.name());

        return data;
    }

//    @Override
//    public Dictionary<String,Integer> computeScore() throws InvalidMatchStateException{
//        if(state != State.FINISH){
//            throw new InvalidMatchStateException();
//        }
//
//        Dictionary<String, Integer> dict = new Hashtable<>();
//        for (IPlayer p : players){
//            dict.put(p.getName(),p.computeScore());
//        }
//        return dict;
//    }
}
