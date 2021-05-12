package takeiteasy.GUI.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

import java.util.Iterator;

public class LocalLobbyCtrl implements IViewController {
    IGame game;
    IViewUpdater vu;
    private String oldName;
    private boolean seedFieldFlag = false;

    @FXML
    ListView<String> playersListView;

    @FXML
    TextField nameField, renameField, seedField;

    @FXML
    Button confirmButton, cancel, submit, rename, remove, start, back, setSeed;

    @FXML
    Label seedLabel;

    @Override
    public void injectGame(IGame g) {
        this.game = g;
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    public void refreshPlayersList(JSONObject gameData) {
        ObservableList<String> playersNameObservable = FXCollections.observableArrayList();
        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        for (int iii = 0; iii < playersData.length(); ++iii){
            String playerName = playersData.getJSONObject(iii).getString("playerName");
            playersNameObservable.add(playerName);
        }
        playersListView.setItems(playersNameObservable);
    }

    @Override
    public void refreshView(JSONObject gameData) {
        refreshPlayersList(gameData);
    }

    void setVisibility(Boolean bool){
        renameField.setVisible(bool);
        confirmButton.setVisible(bool);
        playersListView.setMouseTransparent(bool);
        cancel.setVisible(bool);
        nameField.setMouseTransparent(bool);
        seedField.setMouseTransparent(bool);
        setSeed.setDisable(bool);
        submit.setDisable(bool);
        rename.setDisable(bool);
        remove.setDisable(bool);
        start.setDisable(bool);
        back.setDisable(bool);
    }

    void nameLengthAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setResizable(true);
        errorAlert.setHeaderText("Input not valid");
        errorAlert.setContentText("The name must be between 1 and 10 characters");
        errorAlert.showAndWait();
    }

    boolean checkPlayerNameLength(String name){
        return (name.length() < 11);
    }

    @FXML
    void addNewPlayer() {
        if (!nameField.getText().equals("") && checkPlayerNameLength(nameField.getText())) {
            String name = nameField.getText();
            game.addPlayer(name);

            //TODO: get players name data from JSON object
            JSONObject gameData = game.getData();
            refreshView(gameData);
            nameField.clear();
        }
        else {
            nameLengthAlert();
        }
    }

    @FXML
    void removePlayer() {
        String newName = playersListView.getSelectionModel().getSelectedItem();
        game.removePlayer(newName);
        JSONObject gameData = game.getData();
        refreshView(gameData);
    }

    @FXML
    void confirmRename() {
        if (!renameField.getText().equals("") && checkPlayerNameLength(renameField.getText())) {
            String newName = renameField.getText();
            renamePlayer();

            game.renamePlayer(oldName, newName);
            oldName = "";

            JSONObject gameData = game.getData();
            refreshView(gameData);
            renameField.clear();
            setVisibility(false);
        }
        else {
            nameLengthAlert();
        }
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

    @FXML
    void seedOnMouseClicked(){
        if(!seedFieldFlag) {
            seedField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    seedField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            seedFieldFlag = true;
        }
    }

    @FXML
    void setSeed() {
        if (!seedField.getText().equals("")) {
            String seed = seedField.getText();
            seedLabel.setText(seed);
            game.setMatchSeed(Integer.parseInt(seed));
            seedField.clear();
        }
    }
}
