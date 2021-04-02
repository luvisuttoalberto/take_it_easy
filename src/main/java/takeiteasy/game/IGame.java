package takeiteasy.game;

import takeiteasy.board.HexCoordinates;
import org.json.JSONObject;

public interface IGame {

    enum State{
        MAIN_MENU,
        LOCAL_LOBBY,
        LOCAL_MATCH
    }

    /*
     REMOVE:
     Since we don't want to verify the state of the Game
     from the GUI, we don't implement a getter for the
     state; instead, the state will be written in the
     JSONObject, requested by the GUI to understand
     what it needs to display. The states are still public
     in order to have them available from the GUI (==)
    */

    //main menu
    void createLocalGame();

    //local game setup
    void addPlayer(String name);
    void removePlayer(String name);
    void renamePlayer(String oldName, String newName);
    void startLocalMatch();
    void setMatchSeed(long seed);
    void backToTheMainMenu();

    //Play local match
    void playerPlacesTileAt(String name, HexCoordinates coordinates);
    void backToLocalSetup();
    void endMatch();

    JSONObject getData();

}
