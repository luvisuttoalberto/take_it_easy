package takeiteasy.GUI.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

import java.util.Iterator;

public class LocalLobbyCtrl implements IViewController {
    IGame game;
    IViewUpdater vu;
    private String oldName;

    @Override
    public void injectGame(IGame g) {
        this.game = g;
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    @Override
    public void refreshView(JSONObject gamedata) {
    }

    @FXML
    ListView<String> playersListView;

    @FXML
    TextField nameField;

    @FXML
    TextField renameField;

    @FXML
    Button confirmButton;

    @FXML
    Button cancel;

    @FXML
    Button submit;

    @FXML
    Button rename;

    @FXML
    Button remove;

    @FXML
    Button start;

    @FXML
    Button back;

    void setVisibility(Boolean bool){
        renameField.setVisible(bool);
        confirmButton.setVisible(bool);
        playersListView.setMouseTransparent(bool);
        cancel.setVisible(bool);
        nameField.setMouseTransparent(bool);
        submit.setDisable(bool);
        rename.setDisable(bool);
        remove.setDisable(bool);
        start.setDisable(bool);
        back.setDisable(bool);
    }

    @FXML
    void addNewPlayer() {
        if (!nameField.getText().equals("")) {
            String name = nameField.getText();
            game.addPlayer(name);

            //TODO: get players name data from JSON object
            JSONObject gameData = game.getData();
            Iterator<String> playersNameKeys = gameData.getJSONObject("gameMatch").getJSONObject("players").keys();
            ObservableList<String> playersNameObservable = FXCollections.observableArrayList();
            while(playersNameKeys.hasNext()) {
                String nameKey = playersNameKeys.next();
                playersNameObservable.add(nameKey);
            }
            playersListView.setItems(playersNameObservable);
            nameField.clear();
        }
        vu.updateView();
    }

    @FXML
    void removePlayer() {
        String newName = playersListView.getSelectionModel().getSelectedItem();
        game.removePlayer(newName);
        JSONObject gameData = game.getData();
        Iterator<String> playersNameKeys = gameData.getJSONObject("gameMatch").getJSONObject("players").keys();
        ObservableList<String> playersNameObservable = FXCollections.observableArrayList();
        while(playersNameKeys.hasNext()) {
            String nameKey = playersNameKeys.next();
            playersNameObservable.add(nameKey);
        }
        playersListView.setItems(playersNameObservable);
    }


    @FXML
    void confirmRename() {
        if (!renameField.getText().equals("")) {
            String newName = renameField.getText();
            renamePlayer();

            game.renamePlayer(oldName, newName);
            oldName = "";

            JSONObject gameData = game.getData();
            Iterator<String> playersNameKeys = gameData.getJSONObject("gameMatch").getJSONObject("players").keys();
            ObservableList<String> playersNameObservable = FXCollections.observableArrayList();
            while(playersNameKeys.hasNext()) {
                String nameKey = playersNameKeys.next();
                playersNameObservable.add(nameKey);
            }
            playersListView.setItems(playersNameObservable);
            renameField.clear();
            setVisibility(false);
        }
        //TODO: change visibility inside or outside of the IF
    }

    @FXML
    void renamePlayer() {
        if (playersListView.getSelectionModel().getSelectedItem() != null) {
            oldName = playersListView.getSelectionModel().getSelectedItem();
            setVisibility(true);
        }
    }

    @FXML
    void cancelRename() {
        renameField.clear();
        setVisibility(false);
    }


    @FXML
    void startMatch() {
        game.startLocalMatch();
        vu.updateView();
    }

    @FXML
    void backToMainMenu() {
        game.backToTheMainMenu();
        vu.updateView();
    }

}
