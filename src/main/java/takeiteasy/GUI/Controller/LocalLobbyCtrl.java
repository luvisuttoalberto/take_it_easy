package takeiteasy.GUI.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.game.IGame;

public class LocalLobbyCtrl implements IViewController {
    IGame game;
    IViewUpdater vu;

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
    void addNewPlayer() {
        if (!nameField.getText().equals("")) {
            String name = nameField.getText();
            game.addPlayer(name);

            // following line of code should be replaced by string array of players list, collected from JSON object file
            // this is just example to check functionality
            String[] playersName = {"Azad", "Michele", "Nick", "Alberto"};

            ObservableList<String> playersNameObservable = FXCollections.observableArrayList(playersName);
            playersListView.setItems(playersNameObservable);
            nameField.clear();
        }
        vu.updateView();
    }

    //public  void removePlayer(ActionEvent e) {}
    //playerList.add(nameField.getText());
}
