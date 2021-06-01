package takeiteasy.core.game;

import org.json.JSONObject;
import takeiteasy.core.JSONKeys;
import takeiteasy.core.board.*;
import takeiteasy.core.board.exceptions.*;
import takeiteasy.core.gamematch.*;
import takeiteasy.core.gamematch.exceptions.*;
import takeiteasy.core.player.exceptions.InvalidPlayerStateException;

public class Game implements IGame{
    IGameMatch gameMatch;
    State state = State.MAIN_MENU;

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
        catch (LastPlacingPlayerRemovedException e){
            try{
                gameMatch.dealNextTile();
            }
            catch (Exception ignored){
            }
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
        catch(InvalidMatchStateException | NotEnoughPlayersException ignored){
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
        catch(  PlayerNameNotFoundException | OutOfBoardCoordinatesException | InvalidPlayerStateException |
                 CoordinatesOccupiedException | InvalidMatchStateException | PlayersNotReadyForNextTileException ignored){
        }
        catch(TilePoolDepletedException e){
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

    void endMatch() {
        try{
            gameMatch.endMatch();
        }
        catch (PlayersNotReadyToEndMatchException | TilePoolNotDepletedException | InvalidMatchStateException ignored) {
        }
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();

        data.put(JSONKeys.GAME_STATE, state.name());

        if(gameMatch != null){
            data.put(JSONKeys.GAME_MATCH, gameMatch.getData());
        }

        return data;
    }

}
