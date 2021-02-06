package takeiteasy.game;

import takeiteasy.board.HexCoordinates;
import org.json.JSONObject;

public interface IGame {
    //main menu
    void createLocalGame();
    void exitGame();

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

    JSONObject getData();
}
