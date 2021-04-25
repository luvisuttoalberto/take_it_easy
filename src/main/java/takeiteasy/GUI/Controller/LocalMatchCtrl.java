package takeiteasy.GUI.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.LocalMatchComponents.TileCtrl;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.IGame;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static takeiteasy.utility.Utility.generateCoordinateStandard;

public class LocalMatchCtrl extends GridPane implements IViewController, Initializable {

    public Pane boardPane;
    public Button btn_placeTile;
    public Text text_playerName;
    public Text text_playerStatus;
    public ListView playersPane;
    public Text text_matchStatus;
    IGame game;
    IViewUpdater vu;
    HexCoordinates focusedCoordinates;
    String focusedPlayerName;
    Map<HexCoordinates, TileCtrl> tiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoard();
        //TODO: maybe call here linkUI()
    }

    void buildBoard(){

        tiles = new HashMap<>();

        int[] layout_X = {  52,52,52,
                            111,111,111,111,
                            170,170,170,170,170,
                            229,229,229,229,
                            288,288,288};
        int[] layout_Y = {  87,155,224,
                            53,122,190,259,
                            18,87,155,224,293,
                            53,122,190,259,
                            87,155,224};

        HexCoordinates[] coords = generateCoordinateStandard();

        for(int i = 0; i < 19; i++){
            TileCtrl t = new TileCtrl();
            t.setLayoutX(layout_X[i]);
            t.setLayoutY(layout_Y[i]);

            boardPane.getChildren().add(t);
            tiles.put(coords[i], t);
        }

        //todo: Move this to linkui()? If so, we have to call it somewhere
        btn_placeTile.setOnMouseReleased(e -> onPlaceTileRelease());
//        btn_placeTile.setOnKeyReleased(e -> {
//            if(e.getCode().equals(KeyCode.ENTER)){
//                onPlaceTileRelease();
//            }
//        });
        //TODO: maybe defocus if click outside the board, build specific hitbox
    }

    void focusCoordinates(HexCoordinates coords, JSONObject currentTileData){

        //Do nothing if already focused on this tile
        if (focusedCoordinates == coords) {
            return;
        }

        if (focusedCoordinates != null) {
            //graphic defocus
            tiles.get(focusedCoordinates).resetGraphics();
        }

        focusedCoordinates = coords;

        if (focusedCoordinates == null) {
            btn_placeTile.setDisable(true);
            return;
        }

        //graphic focus
        if(currentTileData != null){
            tiles.get(focusedCoordinates).setFocusedGraphics(
                    currentTileData.getInt("top"),
                    currentTileData.getInt("left"),
                    currentTileData.getInt("right")
            );
        }

        //TODO: activate placing button, probably call refreshview()
//        refreshView(game.getData());
        btn_placeTile.setDisable(false);
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

    void refreshBoard(JSONObject gameData){
        JSONObject playerData = gameData.getJSONObject("gameMatch").getJSONObject("players").getJSONObject(focusedPlayerName);
        JSONObject boardData = playerData.getJSONObject("playerBoard");
        JSONObject currentTileData = gameData.getJSONObject("gameMatch").getJSONObject("currentTile");

        for(HexCoordinates possibleCoord : tiles.keySet()){

            if(boardData.opt(possibleCoord.toString()) != null){
                JSONObject tileData = boardData.getJSONObject(possibleCoord.toString());

                tiles.get(possibleCoord).setPlacedGraphics(tileData.getInt("top"), tileData.getInt("left"), tileData.getInt("right"));

                if(playerData.get("playerState") == "PLACING") {
                    tiles.get(possibleCoord).hitBox.setOnMouseReleased(e -> focusCoordinates(null, null));
                }
                else{
                    tiles.get(possibleCoord).hitBox.setOnMouseReleased(e -> {});
                }
            }
            else{
                tiles.get(possibleCoord).resetGraphics();
                if(playerData.get("playerState") == "PLACING") {
                    tiles.get(possibleCoord).hitBox.setOnMouseReleased(e -> focusCoordinates(possibleCoord, currentTileData));
                }
                else{
                    tiles.get(possibleCoord).hitBox.setOnMouseReleased(e -> {});
                }
            }
        }
    }

    void onFocusPlayerRelease(String playerName){
        focusedPlayerName = playerName;
        refreshView(game.getData());
    }

    void focusNextPlacingPlayer(JSONObject gameData){

        JSONObject playersData = gameData.getJSONObject("gameMatch").getJSONObject("players");
        for(String playerName : playersData.keySet()){
            if(playersData.getJSONObject(playerName).get("playerState").equals("PLACING")){
                focusedPlayerName = playerName;
                break;
            }
        }
    }

    void onPlaceTileRelease(){
        game.playerPlacesTileAt(focusedPlayerName, focusedCoordinates);
        //Note: placement MUST succeed

        //reset focus coordinates
        focusCoordinates(null, null);

        //TODO: should we avoid automatic change to next player?
        focusNextPlacingPlayer(game.getData());
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
    public void refreshView(JSONObject gameData) {

        if(focusedPlayerName == null){
            focusNextPlacingPlayer(gameData);
        }

        //Todo: remove
        System.out.println(gameData);

        refreshBoard(gameData);

        JSONObject focusedPlayerData = gameData.getJSONObject("gameMatch").getJSONObject("players").getJSONObject(focusedPlayerName);
        refreshPlacingButton(focusedPlayerData);

        //player name/status
        text_playerName.setText(focusedPlayerName);
        refreshCurrentPlayerInfo(gameData);

        refreshMatchInfo(gameData);

        // Todo: players list...
//        playersPane = new ListView(FXCollections.observableList(Arrays.asList(gameData.getJSONObject("gameMatch").getJSONObject("players").keySet())));

        // game status text
    }

    void refreshMatchInfo(JSONObject gameData){
        JSONObject matchData = gameData.getJSONObject("gameMatch");
        String matchStateText;
        JSONObject playersData = matchData.getJSONObject("players");

        if(matchData.get("matchState") == "PLAY"){
            int totalNumberOfPlayers = playersData.keySet().size();
            int placingPlayers = 0;
            for(String playerName : playersData.keySet()){
                if(playersData.getJSONObject(playerName).get("playerState") == "PLACING"){
                    placingPlayers++;
                }
            }
            matchStateText = placingPlayers + " players out of " + totalNumberOfPlayers + " are still placing.";
        }
        else{
            int bestScore = 0;
            String winner = "";
            boolean tie = false;

            for(String playerName : playersData.keySet()){
                int currentPlayerScore = playersData.getJSONObject(playerName).getInt("playerScore");
                if(currentPlayerScore > bestScore){
                    bestScore = currentPlayerScore;
                    winner = playerName;
                    tie = false;
                }
                else if(currentPlayerScore == bestScore){
                    tie = true;
                }
            }
            if(tie){
                matchStateText = "TIE!";
            }
            else{
                matchStateText = "The winner is " + winner + " with a score of "+ bestScore + "!";
            }
        }
        text_matchStatus.setText(matchStateText);
    }

    void refreshCurrentPlayerInfo(JSONObject gameData) {
        JSONObject focusedPlayerData = gameData.getJSONObject("gameMatch").getJSONObject("players").getJSONObject(focusedPlayerName);
        JSONObject currentTileData = gameData.getJSONObject("gameMatch").getJSONObject("currentTile");
        if(focusedPlayerData.get("playerState") == "PLACING"){
            text_playerStatus.setText(
                    focusedPlayerData.getString("playerState") + ": (" +
                    currentTileData.get("top") + "," +
                    currentTileData.get("left") + "," +
                    currentTileData.get("right") + ")");
        }
        else{
            text_playerStatus.setText(focusedPlayerData.getString("playerState"));
        }
    }

    void linkUI(){
        //TODO:
    }

}
