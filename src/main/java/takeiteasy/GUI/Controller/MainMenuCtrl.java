package takeiteasy.GUI.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

//TODO: how about implementing the initializable class
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
        //TODO: do we need this function?
    }

    @FXML
    void startLocalMatch(ActionEvent event) {
        game.createLocalLobby();
        vu.updateView();
    }

    //TODO: adding new credits fxml file to redirect to
    @FXML
    void creditsPage(ActionEvent event) {
        System.out.println("Software Development Methods Course Project");
    }

    @FXML
    void exitGame(ActionEvent event) {
        Platform.exit();
    }

    @Override
    public void refreshView(JSONObject gamedata) {
        //TODO: provide a collection of quote of the day or referring to a website.
    }
}
