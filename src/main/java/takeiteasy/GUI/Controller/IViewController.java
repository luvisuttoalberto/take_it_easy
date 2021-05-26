package takeiteasy.GUI.Controller;

import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.core.game.IGame;

public interface IViewController {
    void injectGame(IGame g);
    void injectViewUpdater(IViewUpdater vu);
    void refreshView(JSONObject gameData);
}
