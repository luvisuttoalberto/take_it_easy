package takeiteasy.GUI.Controller;

import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

public class LocalMatchCtrl implements IViewController {

    IGame game;
    IViewUpdater vu;

    @Override
    public void injectGame(IGame g) {
        this.game = g;
        linkUI();
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    @Override
    public void refreshView(JSONObject gamedata) {
        //TODO:
    }

    void linkUI(){
        //TODO:
    }


}
