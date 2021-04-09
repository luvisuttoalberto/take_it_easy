package takeiteasy.game;

import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.gamematch.*;
import takeiteasy.player.InvalidPlayerStateException;

public class Game implements IGame{
    private IGameMatch gameMatch;
//    private String message = "";
    private State state = State.MAIN_MENU;

    //TODO: Sta roba fa schifo ma vabbè lasciamola così che non si sa mai
    @Override
    public void createLocalLobby() {
        if(state == State.MAIN_MENU){
            gameMatch = new GameMatch();
            state = State.LOCAL_LOBBY;
        }
    }

    @Override
    public void addPlayer(String name) {
        try {
            gameMatch.addPlayer(name);
        }
        catch (PlayersWithSameNameNotAllowedException | InvalidMatchStateException ignored){
        }
    }

    @Override
    public void removePlayer(String name) {
        try{
            gameMatch.removePlayer(name);
        }
        catch (PlayerNameNotFoundException | NotEnoughPlayersException ignored){
        }
    }

    @Override
    public void renamePlayer(String oldName, String newName) {
        try{
            gameMatch.setPlayerName(oldName, newName);
        }
        catch(PlayerNameNotFoundException | InvalidMatchStateException | PlayersWithSameNameNotAllowedException ignored){
        }
    }

    @Override
    public void setMatchSeed(long seed) {
        try {
            gameMatch.setTilePoolSeed(seed);
        }
        catch (InvalidMatchStateException ignored){
        }
    }

    @Override
    public void startLocalMatch() {
        try {
            gameMatch.startMatch();
            state = State.LOCAL_MATCH;
        }
        catch(InvalidMatchStateException | NotEnoughPlayersException | InvalidPlayerStateException ignored){
        }
    }

    @Override
    public void backToTheMainMenu() {
        state = State.MAIN_MENU;
        gameMatch = null;
    }

    @Override
    public void playerPlacesTileAt(String name, HexCoordinates coordinates) {
        try {
            gameMatch.positionCurrentTileOnPlayerBoard(name, coordinates);
            gameMatch.dealNextTile();
        }
        catch(  PlayerNameNotFoundException | BadHexCoordinatesException | OutOfBoardCoordinatesException |
                InvalidPlayerStateException | CoordinatesOccupidedException | InvalidMatchStateException |
                NotEnoughPlayersException | PlayersNotReadyForNextTileException ignored){
        }
        catch(TilePoolDepletedException e){
            //TODO: alternatively: copy and paste what's inside of endMatch;
            //      downside: use a try block inside a catch
            endMatch();
        }
    }

    @Override
    public void backToLocalLobby() {
        try {
            gameMatch.backToSetup();
            state = State.LOCAL_LOBBY;
        }
        catch(InvalidMatchStateException ignored){
        }
    }

    private void endMatch() {
        try{
            gameMatch.endMatch();
        }
        catch (PlayersNotReadyToEndMatchException | InvalidPlayerStateException | TilePoolNotDepletedException | InvalidMatchStateException ignored) {
        }
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();

        if(state == State.MAIN_MENU){
            data.put("gameState", state.name());
            return data;
        }

        data = gameMatch.getData();

        data.put("gameState", state.name());

//        if(!message.isBlank()){
//            data.put("message", message);
//            message = "";
//        }

        return data;
    }

}
