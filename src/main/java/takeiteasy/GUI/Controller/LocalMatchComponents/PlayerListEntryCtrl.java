package takeiteasy.GUI.Controller.LocalMatchComponents;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class PlayerListEntryCtrl extends AnchorPane {
    public AnchorPane pane_kickDialog;
    Text text_playerName, text_status;
    public Button btn_showKickDialog, btn_focus, btn_kick_confirm, btn_kick_cancel;

    public PlayerListEntryCtrl(String playerName){
        buildContent(playerName);
    }

    void buildContent(String playerName){

        text_playerName = new Text();
        text_playerName.setText(playerName);

        text_status = new Text();
        text_status.setText("Status");

        btn_focus = new Button();
        btn_focus.setText("View");

        btn_showKickDialog = new Button();
        btn_showKickDialog.setText("X");

        btn_kick_confirm = new Button();
        btn_kick_confirm.setText("Confirm Kick");

        btn_kick_cancel = new Button();
        btn_kick_cancel.setText("Cancel");

        HBox layout_kickDialog = new HBox();
        layout_kickDialog.setAlignment(Pos.CENTER);

        //TODO: kick btns fill all anchorpane
        layout_kickDialog.getChildren().addAll(btn_kick_confirm,btn_kick_cancel);
        HBox.setHgrow(btn_kick_confirm, Priority.ALWAYS);
        HBox.setHgrow(btn_kick_cancel, Priority.ALWAYS);

        pane_kickDialog = new AnchorPane();
        pane_kickDialog.getChildren().add(layout_kickDialog);

        this.getChildren().addAll(btn_focus, btn_showKickDialog, text_status, text_playerName,pane_kickDialog);

        btn_showKickDialog.setDisable(true);
        pane_kickDialog.setVisible(false);

        setTopAnchor(text_playerName, 10.0);
        setLeftAnchor(text_playerName, 10.0);
        setTopAnchor(text_status, 25.0);
        setLeftAnchor(text_status, 10.0);

        setTopAnchor(btn_focus, 17.0);
        setRightAnchor(btn_focus, 26.0);
        setTopAnchor(btn_showKickDialog, 0.0);
        setRightAnchor(btn_showKickDialog, 0.0);

        setTopAnchor(pane_kickDialog, 0.0);
        setLeftAnchor(pane_kickDialog, 0.0);
        setRightAnchor(pane_kickDialog, 0.0);
        setBottomAnchor(pane_kickDialog, 0.0);

        btn_showKickDialog.setOnMouseReleased(e->pane_kickDialog.setVisible(true));
        btn_kick_cancel.setOnMouseReleased(e->pane_kickDialog.setVisible(false));
    }

    //TODO: graphic: change background or text color based on status
    public void setValues(String statusText){
        text_status.setText(statusText);
    }

}
