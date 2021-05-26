package takeiteasy.GUI.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

public class MainMenuCtrl implements IViewController{
    IGame game;
    IViewUpdater vu;

    //Todo: add a title to the main menu and modify credits
    //Todo: instructions?
    @FXML
    AnchorPane creditsPane;

    @Override
    public void injectGame(IGame g){
        this.game=g;
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    @FXML
    void onStartLocalMatchRelease() {
        game.createLocalLobby();
        vu.updateView();
    }

    @FXML
    void onShowCreditsRelease() {
        creditsPane.setVisible(true);
    }

    @FXML
    void onCloseCreditsRelease(){
        creditsPane.setVisible(false);
    }

    @FXML
    void onExitGameRelease() {
        Platform.exit();
    }

    @Override
    public void refreshView(JSONObject gameData) {}
}
