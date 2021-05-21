package takeiteasy.GUI.Controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

import java.net.URL;
import java.util.ResourceBundle;

public class LocalLobbyCtrl implements IViewController, Initializable {
    IGame game;
    IViewUpdater vu;
    private String oldName;
    public final Integer MAX_NAME_LENGTH = 10;

    @FXML
    ListView<String> playersListView;

    @FXML
    TextField nameField, renameField, seedField;

    @FXML
    Button confirmButton, cancel, submit, rename, remove, start, back, setSeed;

    @FXML
    Label seedLabel;

    ObservableList<String> playersNameObservable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        playersNameObservable = FXCollections.observableArrayList();

        playersListView.setItems(playersNameObservable);

        IntegerBinding sizeOfListViewBinding = Bindings.size(playersNameObservable);

        start.disableProperty().bind(
                sizeOfListViewBinding.isEqualTo(0)
        );
        
        setSeed.disableProperty().bind(
                Bindings.isEmpty(seedField.textProperty())
        );

        seedField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    seedField.setText(newValue.replaceAll("[^\\d]", ""));
                }
        });

        rename.disableProperty().bind(
            Bindings.isEmpty(playersListView.getSelectionModel().getSelectedItems())
        );

        renameField.textProperty().addListener((observable, oldValue, newValue) ->
                confirmButton.setDisable(
                                newValue.isEmpty() ||
                                newValue.length() > MAX_NAME_LENGTH ||
                                playersNameObservable.stream().anyMatch(x -> x.contentEquals(newValue))
                )
        );

        nameField.textProperty().addListener((observable, oldValue, newValue) ->
                submit.setDisable(
                                newValue.isEmpty() ||
                                newValue.length() > MAX_NAME_LENGTH ||
                                playersNameObservable.stream().anyMatch(x -> x.contentEquals(newValue))
                )
        );

        remove.disableProperty().bind(
                Bindings.or(
                        Bindings.isEmpty(playersListView.getSelectionModel().getSelectedItems()),
                        Bindings.lessThanOrEqual(sizeOfListViewBinding, 1)
                )
        );
    }

    @Override
    public void injectGame(IGame g) {
        this.game = g;
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    public void refreshPlayersList(JSONObject gameData) {
        playersNameObservable.clear();
        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        for (int iii = 0; iii < playersData.length(); ++iii){
            String playerName = playersData.getJSONObject(iii).getString("playerName");
            playersNameObservable.add(playerName);
        }
        playersListView.refresh();
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
        back.setDisable(bool);
    }

    @FXML
    void addNewPlayer() {
        String name = nameField.getText();
        game.addPlayer(name);

        JSONObject gameData = game.getData();
        refreshView(gameData);
        nameField.clear();
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
        String newName = renameField.getText();
        renamePlayer();

        game.renamePlayer(oldName, newName);
        oldName = "";

        JSONObject gameData = game.getData();
        refreshView(gameData);
        renameField.clear();
        setVisibility(false);
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
    void setSeed() {
        if (!seedField.getText().equals("")) {
            String seed = seedField.getText();
            seedLabel.setText(seed);
            game.setMatchSeed(Integer.parseInt(seed));
            seedField.clear();
        }
    }

}
