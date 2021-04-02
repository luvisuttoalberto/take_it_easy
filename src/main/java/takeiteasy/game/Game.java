package takeiteasy.game;

import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.gamematch.*;
import takeiteasy.player.InvalidPlayerStateException;
import takeiteasy.tilepool.Tile;

import static takeiteasy.utility.Utility.generateCoordinateStandard;

public class Game implements IGame{
    private IGameMatch gameMatch;
    private String message = "";
    private State state = State.MAIN_MENU;

    @Override
    public void createLocalGame() {
        gameMatch = new GameMatch();
        state = State.LOCAL_LOBBY;
    }

    @Override
    public void addPlayer(String name) {
        try {
            gameMatch.addPlayer(name);
        }
        catch (PlayersWithSameNameNotAllowedException e) {
            message = "Player not added, a player with this name is already present";
        }
        catch (InvalidMatchStateException ignored){
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
        catch(PlayerNameNotFoundException | InvalidMatchStateException ignored){
        }
        catch(PlayersWithSameNameNotAllowedException e){
            message = "Player name not changed, a player with this name is already present";
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
        try {
            gameMatch.endMatch();
            state = State.MAIN_MENU;
        }
        catch(PlayersNotReadyToEndMatchException | InvalidMatchStateException | TilePoolNotDepletedException | InvalidPlayerStateException ignored) {
        }
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
            message = "Tilepool depleted";
        }
    }

    @Override
    public void backToLocalSetup() {
        try {
            gameMatch.backToSetup();
            state = State.LOCAL_LOBBY;
        }
        catch(InvalidMatchStateException ignored){
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

        if(!message.isBlank()){
            data.put("message", message);
            message = ""; //TODO: for network implementation: this should be adapted, at least in the "Tilepool depleted" case
        }

        return data;
    }

}
