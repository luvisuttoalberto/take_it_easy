package takeiteasy.GUI.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.core.game.IGame;

public class MainMenuCtrl implements IViewController{
    IGame game;
    IViewUpdater vu;

    @FXML
    AnchorPane creditsPane;

    @FXML
    ScrollPane howToPlayPane;

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
    void onShowHowToPlayRelease(){
        howToPlayPane.setVisible(true);
    }

    @FXML
    void onCloseHowToPlayRelease(){
        howToPlayPane.setVisible(false);
    }

    @FXML
    void onExitGameRelease() {
        Platform.exit();
    }

    @Override
    public void refreshView(JSONObject gameData) {}

}
