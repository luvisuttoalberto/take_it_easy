package takeiteasy.GUI.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.LocalMatchComponents.TileCtrl;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.board.BadHexCoordinatesException;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.IGame;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LocalMatchCtrl extends GridPane implements IViewController, Initializable {

    public Pane boardPane;
    public Button btn_placeTile;
    IGame game;
    IViewUpdater vu;
    HexCoordinates focusedCoordinates;
    String focusedPlayerName;
    Map<HexCoordinates, TileCtrl> tiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoard();
    }

    void buildBoard(){
        //TODO:cycle all

        tiles = new HashMap<>();

        TileCtrl t = new TileCtrl();

        //Todo: position tile
        try{
            HexCoordinates coords = new HexCoordinates(0,0,0);
            t.setLayoutX(170);
            t.setLayoutY(156);
            HexCoordinates finalCoords = coords;
            t.hitBox.setOnMouseReleased(e -> focusCoordinates(finalCoords));
            boardPane.getChildren().add(t);

            tiles.put(coords, t);
        } catch (BadHexCoordinatesException e) {
            e.printStackTrace();
        }

        TileCtrl t2 = new TileCtrl();

        //Todo: position tile
        try{
            HexCoordinates coords = new HexCoordinates(1,1,-2);
            t2.setLayoutX(70);
            t2.setLayoutY(56);
            HexCoordinates finalCoords = coords;
            t2.hitBox.setOnMouseReleased(e -> focusCoordinates(finalCoords));
            boardPane.getChildren().add(t2);

            tiles.put(coords, t2);
        } catch (BadHexCoordinatesException e) {
            e.printStackTrace();
        }

    }

    void focusCoordinates(HexCoordinates coords){

        //TODO: remove
        System.out.println(coords.toString());

        //Do nothing if already focused on this tile
        if (focusedCoordinates == coords) {
            return;
        }

        if (focusedCoordinates != null) {
            //graphic defocus
            tiles.get(focusedCoordinates).unfocus();
        }

        focusedCoordinates = coords;

        if (focusedCoordinates == null) {
            return;
        }

        //graphic focus
        tiles.get(focusedCoordinates).focus();

        //TODO: activate placing button

    }

    void refreshPlacingButton(JSONObject playerData){

        //todo: magic fields, maybe add a graphic change on the text or background
        if(focusedCoordinates != null && playerData.get("playerState") == "PLACING"){
            btn_placeTile.setDisable(false);
        }
        else{
            btn_placeTile.setDisable(true);
        }

    }

    void refreshBoard(JSONObject playerData){

        //TODO: TLR numbers
        //TODO: enable/disable tiles basing on playerStatus

        try{
            TileCtrl t = tiles.get(new HexCoordinates(0,0,0));
            t.setValues(1,2,3);
        }
        catch (Exception ignored){
        }

    }

    void onFocusPlayerRelease(String playerName){
        focusedPlayerName = playerName;
        refreshView(game.getData());
    }

    void focusNextPlacingPlayer(){
        //TODO: compute next player
        String nextPlayer = "asd";
        focusedPlayerName = nextPlayer;
    }

    void onPlaceTileRelease(){
        game.playerPlacesTileAt(focusedPlayerName, focusedCoordinates);
        //Note: placement MUST succeed

        //reset focus coordinates
        focusCoordinates(null);

        focusNextPlacingPlayer();
        refreshView(game.getData());
    }

    @Override
    public void injectGame(IGame g) {
        this.game = g;
        linkUI();
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    @Override
    public void refreshView(JSONObject gamedata) {
        //TODO: focused player stuff

        refreshBoard(gamedata.getJSONObject("gameMatch").getJSONObject("players").getJSONObject("Dario"));

        //player name/status
        refreshPlacingButton(gamedata.getJSONObject("gameMatch").getJSONObject("players").getJSONObject("Dario"));

        //TODO: game stuff
        // players list...
        // game status text
    }

    void linkUI(){
        //TODO:
    }

}
