package takeiteasy.game;

import org.json.JSONObject;
import takeiteasy.board.HexCoordinates;
import takeiteasy.gamematch.*;
import takeiteasy.player.InvalidPlayerStateException;

public class Game implements IGame{
    private GameMatch gameMatch;

    @Override
    public void createLocalGame() {
        gameMatch = new GameMatch();
    }

    @Override
    public void exitGame() {
        //TODO: Remove???
    }

    @Override
    public void addPlayer(String name) {
        try {
            gameMatch.addPlayer(name);
        }
        catch (PlayerWithSameNameCannotBeAddedException | InvalidMatchStateException ignored) {
            //TODO: Do we need to handle the exception???
        }
    }

    @Override
    public void removePlayer(String name) {
        try{
            gameMatch.removePlayer(name);
        }
        catch (PlayerNameNotFoundException ignored){
        }
        catch (NotEnoughPlayersException e){
            try {
                gameMatch.backToSetup();
            }
            catch(InvalidMatchStateException ignored){
                //TODO: check if this is good practice
            }
        }
    }

    @Override
    public void renamePlayer(String oldName, String newName) {
        try{
            gameMatch.setPlayerName(oldName, newName);
        }
        catch(PlayerNameNotFoundException | InvalidMatchStateException ingored){
            //TODO: Do we need to handle the exception???
        }
    }

    @Override
    public void startLocalMatch() {
        try {
            gameMatch.startMatch();
        }
        catch(InvalidMatchStateException | NotEnoughPlayersException | InvalidPlayerStateException ignored){
            //TODO: Do we need to handle the exception (InvalidPlayerStateException)???
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
