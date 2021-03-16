package takeiteasy.GUI.Controller;

import javafx.scene.layout.Pane;
import org.json.JSONObject;
import takeiteasy.board.HexCoordinates;

public class BoardCtrl extends Pane {

    private HexCoordinates focusedCoordinates = null;

    public HexCoordinates getFocusedCoordinates(){
        return focusedCoordinates;
    }

    public void switchToPlayer(JSONObject gameData, String playerName){

    }

    void setFocusedCoordinates(HexCoordinates coord){
        this.focusedCoordinates = coord;
    }

    void buildBoard(){

        this.getChildren().addAll();
    }

}
