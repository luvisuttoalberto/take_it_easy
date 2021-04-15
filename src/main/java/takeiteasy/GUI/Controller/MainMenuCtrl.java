package takeiteasy.GUI.Controller;

import javafx.application.Platform;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

public class MainMenuCtrl implements IViewController{
    IGame game;
    IViewUpdater vu;

    @Override
    public void injectGame(IGame g){
        this.game=g;
        linkUI();
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    void linkUI(){
        //TODO:link fxml objs actions to callbacks
    }
    public void onStartLocalBtnRelease() {
        game.createLocalLobby();
        vu.updateView();
    }
    public void onExitBtnRelease() {
        Platform.exit();
    }

    @Override
    public void refreshView(JSONObject gamedata) {
        //MOTD?
        //VERSION?
    }
}
