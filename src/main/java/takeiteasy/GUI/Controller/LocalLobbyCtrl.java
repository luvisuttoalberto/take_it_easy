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
import takeiteasy.JSONKeys;
import takeiteasy.game.IGame;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class LocalLobbyCtrl implements IViewController, Initializable {
    IGame game;
    IViewUpdater vu;
    public final Integer MAX_NAME_LENGTH = 10;
    final String TOOLTIPTEXT_RENAMEFIELD = "Name must be between 1 and " + MAX_NAME_LENGTH +" character and must be unique.";

    Tooltip tt_textField_renamePlayer, tt_textField_newPlayer;

    @FXML ListView<String> playersListView;

    @FXML TextField textField_newPlayer, textField_renamePlayer, textField_seed;

    @FXML Button btn_renameConfirm, btn_renameCancel, btn_addNewPlayer,
            btn_renamePanelShow, btn_removePlayer, btn_startMatch, btn_backToMenu, btn_setSeed;

    @FXML Label label_seed;

    ObservableList<String> playerNamesObservable;

    Boolean isNameInvalid(String name){
        return name.isEmpty() ||
                name.length() > MAX_NAME_LENGTH ||
                playerNamesObservable.stream().anyMatch(x -> x.contentEquals(name));
    }

    void setTooltipOnTextField(TextField textField, Tooltip tooltip, Button button){
        textField.setTooltip(tooltip);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    boolean nameIsInvalid = isNameInvalid(newValue);
                    button.setDisable(nameIsInvalid);
                    if(nameIsInvalid){
                        tooltip.show(textField,
                                textField.localToScene(0.0, 0.0).getX()
                                        + textField.getScene().getX()
                                        + textField.getScene().getWindow().getX(),
                                textField.localToScene(0.0, 0.0).getY()
                                        + textField.getScene().getY()
                                        + textField.getScene().getWindow().getY()
                                        - textField.getHeight());
                        textField.setStyle("-fx-background-color: yellow; -fx-text-fill: red;");
                    }
                    else{
                        textField.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                        tooltip.hide();
                    }
                }
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        playerNamesObservable = FXCollections.observableArrayList();

        playersListView.setItems(playerNamesObservable);

        IntegerBinding sizeOfListViewBinding = Bindings.size(playerNamesObservable);

        btn_startMatch.disableProperty().bind(
                sizeOfListViewBinding.isEqualTo(0)
        );
        
        btn_setSeed.disableProperty().bind(
                Bindings.isEmpty(textField_seed.textProperty())
        );

        textField_seed.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    textField_seed.setText(newValue.replaceAll("[^\\d]", ""));
                }
        });

        btn_renamePanelShow.disableProperty().bind(
            Bindings.isEmpty(playersListView.getSelectionModel().getSelectedItems())
        );

        tt_textField_renamePlayer = new Tooltip();
        tt_textField_renamePlayer.setText(TOOLTIPTEXT_RENAMEFIELD);
        setTooltipOnTextField(textField_renamePlayer, tt_textField_renamePlayer, btn_renameConfirm);

        tt_textField_newPlayer = new Tooltip();
        tt_textField_newPlayer.setText(TOOLTIPTEXT_RENAMEFIELD);
        setTooltipOnTextField(textField_newPlayer, tt_textField_newPlayer, btn_addNewPlayer);

        btn_removePlayer.disableProperty().bind(
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
        playerNamesObservable.clear();
        JSONArray playersData = gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONArray(JSONKeys.MATCH_PLAYERS);

        IntStream.range(0, playersData.length())
                .mapToObj(iii -> playersData.getJSONObject(iii).getString(JSONKeys.PLAYER_NAME))
                .forEach(playerName -> playerNamesObservable.add(playerName));
    }

    void resetTooltips(){
        textField_newPlayer.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        tt_textField_newPlayer.hide();
        textField_renamePlayer.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        tt_textField_renamePlayer.hide();
    }

    @Override
    public void refreshView(JSONObject gameData) {
        refreshPlayersList(gameData);
        resetTooltips();
    }

    void setRenamePanelVisibility(Boolean isVisible){
        textField_renamePlayer.setVisible(isVisible);
        btn_renameConfirm.setVisible(isVisible);
        playersListView.setMouseTransparent(isVisible);
        btn_renameCancel.setVisible(isVisible);
        textField_newPlayer.setMouseTransparent(isVisible);
        textField_seed.setMouseTransparent(isVisible);
        if(!isVisible){
            textField_renamePlayer.setStyle("-fx-background-color: white; -fx-text-fill: black;");
            tt_textField_renamePlayer.hide();
        }
    }

    @FXML
    void onAddNewPlayerRelease() {
        String name = textField_newPlayer.getText();
        game.addPlayer(name);

        textField_newPlayer.clear();
        refreshView(game.getData());
    }

    @FXML
    void onRemovePlayerRelease() {
        String newName = playersListView.getSelectionModel().getSelectedItem();
        game.removePlayer(newName);
        refreshView(game.getData());
    }

    @FXML
    void onRenameConfirmRelease() {
        String newName = textField_renamePlayer.getText();

        game.renamePlayer(playersListView.getSelectionModel().getSelectedItem(), newName);

        textField_renamePlayer.clear();
        setRenamePanelVisibility(false);
        refreshView(game.getData());
    }

    @FXML
    void onRenamePanelShowRelease() {
        setRenamePanelVisibility(true);
    }

    @FXML
    void onRenameCancelRelease() {
        textField_renamePlayer.clear();
        setRenamePanelVisibility(false);
    }

    @FXML
    void onStartMatchRelease() {
        resetTooltips();
        game.startLocalMatch();
        vu.updateView();
    }

    @FXML
    void onBackToMenuRelease() {
        resetTooltips();
        game.backToTheMainMenu();
        vu.updateView();
    }

    @FXML
    void onSetSeedRelease() {
        String seed = textField_seed.getText();
        label_seed.setText(seed);
        game.setMatchSeed(Integer.parseInt(seed));
        textField_seed.clear();
    }
}
