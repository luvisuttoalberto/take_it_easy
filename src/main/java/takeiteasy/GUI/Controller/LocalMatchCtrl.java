package takeiteasy.GUI.Controller;

import javafx.scene.layout.GridPane;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.LocalMatchComponents.TileCtrl;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.board.BadHexCoordinatesException;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.IGame;
import takeiteasy.player.IPlayer;

import java.util.Dictionary;

public class LocalMatchCtrl extends GridPane implements IViewController {

    IGame game;
    IViewUpdater vu;
    HexCoordinates focusedCoordinates;
    String focusedPlayerName;
    Dictionary<HexCoordinates, TileCtrl> tiles;

    void buildBoard(){
        //TODO:cycle all

        TileCtrl t = new TileCtrl();

        //Todo: position tile
        try{
            HexCoordinates coords = new HexCoordinates(0,0,0);
            t.setLayoutX(7);
            t.setLayoutY(7);
            this.getChildren().add(t);

            tiles.put(coords,t);
        } catch (BadHexCoordinatesException e) {
            e.printStackTrace();
        }

    }

    void focusCoordinates(HexCoordinates coords){

        if (focusedCoordinates == coords) {
            return;
            //TODO: is this right?
        }

        if (focusedCoordinates != null) {
            //TODO: graphic defocus
        }

        focusedCoordinates = coords;

        if (focusedCoordinates == null) {
            return;
        }

        //TODO: graphic focus
        //TODO: activate placing button
    }

    void refreshBoard(JSONObject playerData){
        //TODO: TLR numbers
        //TOOO: enable/disable tiles basing on playerStatus
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

        refreshBoard(gamedata.getJSONObject("Players").getJSONObject(focusedPlayerName));
        
        //player name/status
        //placing button
        //TODO: game stuff
        // players list...
        // game status text
    }

    void linkUI(){
        //TODO:
    }


}
