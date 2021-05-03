package takeiteasy.GUI.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.LocalMatchComponents.PlayerListEntryCtrl;
import takeiteasy.GUI.Controller.LocalMatchComponents.TileCtrl;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.IGame;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static takeiteasy.utility.Utility.generateCoordinateStandard;

public class LocalMatchCtrl extends GridPane implements IViewController, Initializable {

    public Pane boardPane;

    public Button btn_placeTile;

    public Text text_playerName;
    public Text text_playerStatus;
    public Text text_matchStatus;

    public ScrollPane playersPane;
    public VBox playersList;

    IGame game;
    IViewUpdater vu;
    HexCoordinates focusedCoordinates;
    String focusedPlayerName;
    Map<HexCoordinates, TileCtrl> tiles;
    Map<String, PlayerListEntryCtrl> players;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoard();
        //buildPlayersList();
        //TODO: maybe call here linkUI()
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

    void buildBoard(){

        double tileWidth = 80;

        double tileHeight = tileWidth*.5*1.732,
                boardXUnit = tileWidth*.75,
                boardYUnit = tileHeight;

        tiles = new HashMap<>();

        for(HexCoordinates coords : generateCoordinateStandard()){
            TileCtrl t = new TileCtrl(tileWidth,tileHeight);

            double x = coords.getX()+2,
                    y = 2-(coords.getY()+coords.getX()*.5);

            t.setLayoutX(boardXUnit * x);
            t.setLayoutY(boardYUnit * y);

            boardPane.getChildren().add(t);
            tiles.put(coords, t);
        }

        //todo: Move this to linkui()? If so, we have to call it somewhere
        btn_placeTile.setOnMouseReleased(e -> onPlaceTileRelease());
    }

    void buildPlayersList(JSONObject gameData){
        players = new HashMap<>();

        JSONObject playersData = gameData.getJSONObject("gameMatch").getJSONObject("players");
        for (String playerName : playersData.keySet()){

            PlayerListEntryCtrl pe = new PlayerListEntryCtrl(playerName);
            players.put(playerName,pe);
            pe.btn_focus.setOnMouseReleased(e -> onFocusPlayerRelease(playerName));
            pe.btn_kick.setOnMouseReleased(e -> onKickPlayerRelease(playerName));
            playersList.getChildren().add(pe);
        }
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

        btn_placeTile.setDisable(false);
    }

    ArrayList<String> computeHighestScoringPlayerNames(JSONObject gameData){
        JSONObject playersData = gameData.getJSONObject("gameMatch").
                getJSONObject("players");

        ArrayList<String> winners = new ArrayList<>();
        int bestScore = 0;
        for(String playerName : playersData.keySet()){
            int currentPlayerScore = playersData.getJSONObject(playerName).
                    getInt("playerScore");
            if(currentPlayerScore > bestScore){
                bestScore = currentPlayerScore;
                winners.clear();
                winners.add(playerName);
            }
            else if(currentPlayerScore == bestScore){
                winners.add(playerName);
            }
        }
        return winners;
    }


    void onFocusPlayerRelease(String playerName){
        focusedPlayerName = playerName;
        refreshView(game.getData());
    }

    void onKickPlayerRelease(String playerName){
        //TODO: pop-up or other double check
        game.removePlayer(playerName);

        playersList.getChildren().remove(players.get(playerName));
        players.remove(playerName);
        focusedPlayerName = null;

        refreshView(game.getData());
    }


    void onPlaceTileRelease(){
        game.playerPlacesTileAt(focusedPlayerName, focusedCoordinates);
        //Note: placement MUST succeed

        //reset focus coordinates
        focusCoordinates(null, null);

        //TODO: should we avoid automatic change to next player?
        JSONObject gameData = game.getData();

        if(gameData.getJSONObject("gameMatch").getString("matchState") == "FINISH"){
            focusedPlayerName = computeHighestScoringPlayerNames(gameData).get(0);
        } else {
            focusNextPlacingPlayer(gameData);
        }
        refreshView(gameData);
    }


    void refreshPlayersList(JSONObject gameData){
        JSONObject matchData = gameData.getJSONObject("gameMatch");
        JSONObject playersData = matchData.getJSONObject("players");
        for (String playerName : players.keySet()){
            JSONObject playerData = playersData.getJSONObject(playerName);
            PlayerListEntryCtrl player = players.get(playerName);

            String statusText;
            if(matchData.getString("matchState") == "FINISH"){
                statusText = String.valueOf(playerData.getInt("playerScore"));
            }
            else{
                statusText = playerData.getString("playerState");
            }
            player.setValues(statusText);

            player.btn_focus.setDisable(playerName == focusedPlayerName);
            player.btn_kick.setDisable(players.size()<2 || matchData.getString("matchState") == "FINISH");
        }

    }

    void refreshPlacingButton(JSONObject gameData){

        JSONObject playerData = gameData.getJSONObject("gameMatch").
                getJSONObject("players").
                getJSONObject(focusedPlayerName);

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
            matchStateText = placingPlayers + " player"+
                    (placingPlayers>1?"s":"") +
                    " out of " + totalNumberOfPlayers + " are still placing.";
        }
        // else match is finished
        else{
            ArrayList<String> winners = computeHighestScoringPlayerNames(gameData);

            if(winners.size()>1){
                matchStateText = "TIE!";
            }
            else{
                int bestScore = gameData.getJSONObject("gameMatch").
                        getJSONObject("players").
                        getJSONObject(winners.get(0)).
                        getInt("playerScore");
                matchStateText = "The winner is " + winners.get(0) +
                        " with a score of "+ bestScore + "!";
            }
        }


        text_matchStatus.setText(matchStateText);
    }

    void refreshCurrentPlayerInfo(JSONObject gameData) {

        text_playerName.setText(focusedPlayerName);

        //TODO: Reformat state text
        //TODO: Reformat Json fields

        //Refresh state
        JSONObject focusedPlayerData = gameData.getJSONObject("gameMatch").
                getJSONObject("players").
                getJSONObject(focusedPlayerName);

        JSONObject currentTileData = gameData.getJSONObject("gameMatch").
                getJSONObject("currentTile");

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

    @Override
    public void refreshView(JSONObject gameData) {

        //TODO: method for first refresh instead of these ifs?

        if(focusedPlayerName == null){
            focusNextPlacingPlayer(gameData);
        }

        if(players == null){
            //DEBUG
            System.out.println("Initializing players list!");
            buildPlayersList(gameData);
        }

        //Todo: remove
        System.out.println(gameData);

        refreshBoard(gameData);
        refreshPlayersList(gameData);
        refreshPlacingButton(gameData);
        refreshCurrentPlayerInfo(gameData);
        refreshMatchInfo(gameData);

    }

    void linkUI(){
        //TODO:
    }

}
