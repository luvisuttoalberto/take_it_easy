package takeiteasy.game;

import takeiteasy.board.HexCoordinates;
import org.json.JSONObject;

public interface IGame {

    enum State{
        MAIN_MENU,
        LOCAL_LOBBY,
        LOCAL_MATCH
    }

    //main menu
    void createLocalLobby();

    //local game setup
    void addPlayer(String name);
    void removePlayer(String name);
    void renamePlayer(String oldName, String newName);
    void startLocalMatch();
    void setMatchSeed(long seed);
    void backToTheMainMenu();

    //Play local match
    void playerPlacesTileAt(String name, HexCoordinates coordinates);
    void backToLocalLobby();

    JSONObject getData();

}
