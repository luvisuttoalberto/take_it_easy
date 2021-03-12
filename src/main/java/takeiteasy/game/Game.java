package takeiteasy.game;

import org.json.JSONObject;
import takeiteasy.board.HexCoordinates;
import takeiteasy.gamematch.*;
import takeiteasy.player.InvalidPlayerStateException;

public class Game implements IGame{
    private GameMatch gameMatch;
    private String message;
    // insert state

    @Override
    public void createLocalGame() {
        gameMatch = new GameMatch();
    }

    @Override
    public void addPlayer(String name) {
        try {
            gameMatch.addPlayer(name);
        }
        catch (PlayerWithSameNameCannotBeAddedException | InvalidMatchStateException ignored) {
            message = "Player not added, a player with this name is already present";
        }
    }

    @Override
    public void removePlayer(String name) {
        try{
            gameMatch.removePlayer(name);
        }
        catch (PlayerNameNotFoundException ignored){
        }
        catch (NotEnoughPlayersException ignored){
            //TODO: maybe display a message on the JsonObject
        }
    }

    @Override
    public void renamePlayer(String oldName, String newName) {
        try{
            gameMatch.setPlayerName(oldName, newName);
        }
        catch(PlayerNameNotFoundException | InvalidMatchStateException ignored){
            //TODO: Display message on JSONObject
        }
    }

    @Override
    public void startLocalMatch() {
        try {
            gameMatch.startMatch();
        }
        catch(InvalidMatchStateException | NotEnoughPlayersException | InvalidPlayerStateException ignored){
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
    public void backToTheMainMenu() {

    }

    @Override
    public void playerPlacesTileAt(String name, HexCoordinates coordinates) {

    }

    @Override
    public void backToLocalSetup() {

    }

    @Override
    public JSONObject getData() {
        return null;
    }
}
