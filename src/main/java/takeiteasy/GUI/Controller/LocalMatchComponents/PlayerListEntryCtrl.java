package takeiteasy.GUI.Controller.LocalMatchComponents;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class PlayerListEntryCtrl extends AnchorPane {
    Text text_playerName, text_status;
    public Button btn_kick, btn_focus;

    public PlayerListEntryCtrl(String playerName){
        buildContent(playerName);
    }

    void buildContent(String playerName){

        text_playerName = new Text();
        text_status = new Text();
        btn_focus = new Button();
        btn_kick = new Button();

        this.getChildren().addAll(btn_focus, btn_kick, text_status, text_playerName);

        btn_kick.setDisable(true);

        text_status.setText("Status");
        //TODO: check player length? Probably not
        text_playerName.setText(playerName);
        btn_focus.setText("View");
        btn_kick.setText("X");

        setTopAnchor(text_playerName, 10.0);
        setLeftAnchor(text_playerName, 10.0);
        setTopAnchor(text_status, 25.0);
        setLeftAnchor(text_status, 10.0);

        setTopAnchor(btn_focus, 17.0);
        setRightAnchor(btn_focus, 26.0);
        setTopAnchor(btn_kick, 0.0);
        setRightAnchor(btn_kick, 0.0);

        //TODO: text limits

    }

    //TODO: graphics, scores
    public void setValues(String status){
        text_status.setText(status);
    }

}
