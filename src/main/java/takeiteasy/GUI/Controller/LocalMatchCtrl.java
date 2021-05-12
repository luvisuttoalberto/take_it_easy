package takeiteasy.GUI.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.LocalMatchComponents.PlayerListEntryCtrl;
import takeiteasy.GUI.Controller.LocalMatchComponents.TileCtrl;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.IGame;
import java.net.URL;
import java.util.*;

import static takeiteasy.utility.Utility.generateCoordinateStandard;

//TODO: Reformat Json fields


public class LocalMatchCtrl extends GridPane implements IViewController, Initializable {

    public Pane pane_boardPane;

    public Button btn_placeTile;

    public Text text_playerName;
    public Text text_playerStatus;
    public Text text_matchStatus;

    public ScrollPane pane_playersPane;
    public VBox layout_playersPane;
    public Pane pane_currentTile;
    public Button btn_backToLobby;
    public Button btn_backToMenu;
    public Button btn_rematch;
    public Button btn_rematchNewSeed;
    public HBox pane_rematchPanel;

    IGame game;
    IViewUpdater vu;
    HexCoordinates focusedCoordinates;
    String focusedPlayerName;
    Map<HexCoordinates, TileCtrl> tiles;
    Map<String, PlayerListEntryCtrl> players;
    TileCtrl currentTileCtrl;

    final double tileWidth = 80;
    final double tileHeight = tileWidth *.5*1.732;

    final String txt_statusPlacing = "Placing";
    final String txt_statusWaitOther = "Waiting";

    void linkStaticButtons(){
        btn_placeTile.setOnMouseReleased(e -> onPlaceTileRelease());
        btn_backToLobby.setOnMouseReleased(e -> onBackToLobbyRelease());
        btn_backToMenu.setOnMouseReleased(e -> onBackToMenuRelease());
        btn_rematch.setOnMouseReleased(e -> onRematchRelease());
        btn_rematchNewSeed.setOnMouseReleased(e -> onRematchNewSeedRelease());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        buildBoard();
        buildCurrentTilePane();
        linkStaticButtons();
        btn_rematch.setText("Rematch\n(SAME tile pool)");
        btn_rematchNewSeed.setText("Rematch\n(new tile pool)");
    }

    @Override
    public void injectGame(IGame g) {
        this.game = g;
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    void buildBoard(){

        double boardXUnit = tileWidth*.75;
        double boardYUnit = tileHeight;

        tiles = new HashMap<>();

        for(HexCoordinates coords : generateCoordinateStandard()){
            TileCtrl t = new TileCtrl(tileWidth,tileHeight);

            //Todo: refactor: extract method?
            double x = coords.getX() + 2;
            double y = 2-(coords.getY()+coords.getX()*.5);

            t.setLayoutX(boardXUnit * x);
            t.setLayoutY(boardYUnit * y);

            pane_boardPane.getChildren().add(t);
            tiles.put(coords, t);
        }
    }

    void buildCurrentTilePane() {
        currentTileCtrl = new TileCtrl(tileWidth, tileHeight);
        pane_currentTile.getChildren().add(currentTileCtrl);
    }

    void buildPlayersList(JSONObject gameData){
        players = new HashMap<>();

        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        for (int iii = 0; iii < playersData.length(); ++iii){

            String playerName = playersData.getJSONObject(iii).getString("playerName");
            PlayerListEntryCtrl pe = new PlayerListEntryCtrl(playerName);

            players.put(playerName, pe);
            pe.btn_focus.setOnMouseReleased(e -> onFocusPlayerRelease(playerName));
            pe.btn_kick_confirm.setOnMouseReleased(e -> onKickPlayerRelease(playerName));
            layout_playersPane.getChildren().add(pe);
        }
    }

    void defocusCoordinates(){
        if (focusedCoordinates != null) {
            //graphic defocus
            tiles.get(focusedCoordinates).resetGraphics();
            focusedCoordinates = null;
            btn_placeTile.setDisable(true);
        }
    }

    void focusCoordinates(HexCoordinates coords, JSONObject currentTileData){

        //Do nothing if already focused on this tile
        if (focusedCoordinates == coords) {
            return;
        }

        defocusCoordinates();

        focusedCoordinates = coords;

        if(currentTileData != null){
            tiles.get(focusedCoordinates).setFocusedGraphics(
                currentTileData.getInt("top"),
                currentTileData.getInt("left"),
                currentTileData.getInt("right")
            );
        }

        btn_placeTile.setDisable(false);
    }

    void focusPlayerAndDefocusCoordinates(String playerName){
        focusedPlayerName = playerName;
        defocusCoordinates();
    }

    void focusFirstPlacingPlayer(JSONObject gameData){

        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        for(int iii = 0; iii < playersData.length(); ++iii){
            if(playersData.getJSONObject(iii).get("playerState").equals("PLACING")){
                focusPlayerAndDefocusCoordinates(playersData.getJSONObject(iii).getString("playerName"));
                break;
            }
        }
    }

    ArrayList<String> computeHighestScoringPlayerNames(JSONObject gameData){
        JSONArray playersData = gameData.getJSONObject("gameMatch").
                getJSONArray("players");

        ArrayList<String> winners = new ArrayList<>();
        int bestScore = 0;
        for(int iii = 0; iii < playersData.length(); ++iii){
            int currentPlayerScore = playersData.getJSONObject(iii).
                    getInt("playerScore");
            if(currentPlayerScore > bestScore){
                bestScore = currentPlayerScore;
                winners.clear();
                winners.add(playersData.getJSONObject(iii).getString("playerName"));
            }
            else if(currentPlayerScore == bestScore){
                winners.add(playersData.getJSONObject(iii).getString("playerName"));
            }
        }
        return winners;
    }

    void onFocusPlayerRelease(String playerName){
        focusPlayerAndDefocusCoordinates(playerName);
        refreshView(game.getData());
    }

    void onKickPlayerRelease(String playerName){
        game.removePlayer(playerName);

        layout_playersPane.getChildren().remove(players.get(playerName));
        players.remove(playerName);

        JSONObject gameData = game.getData();
        if(playerName.equals(focusedPlayerName)){
            focusFirstPlacingPlayer(gameData);
        }
        refreshView(gameData);
    }


    void onPlaceTileRelease(){
        game.playerPlacesTileAt(focusedPlayerName, focusedCoordinates);

        defocusCoordinates();

        JSONObject gameData = game.getData();

        if(gameData.getJSONObject("gameMatch").getString("matchState") == "FINISH"){
            focusPlayerAndDefocusCoordinates(computeHighestScoringPlayerNames(gameData).get(0));
        } else {
            focusFirstPlacingPlayer(gameData);
        }
        refreshView(gameData);
    }

    void onBackToLobbyRelease(){
        game.backToLocalLobby();
        vu.updateView();
    }

    void onBackToMenuRelease(){
        game.backToTheMainMenu();
        vu.updateView();
    }


    void onRematchRelease() {
        game.backToLocalLobby();
        game.startLocalMatch();
        refreshView(game.getData());
    }

    void onRematchNewSeedRelease() {
        game.backToLocalLobby();
        game.setMatchSeed(new Random().nextLong());
        game.startLocalMatch();
        refreshView(game.getData());
    }

    JSONObject getPlayerDataFromPlayerName(JSONArray playersData, String playerName){
        JSONObject playerData = null;
        for(int iii = 0; iii < playersData.length(); ++iii){
            playerData = playersData.getJSONObject(iii);
            if(playerData.getString("playerName").equals(playerName)){
                break;
            }
        }
        return playerData;
    }

    void refreshCurrentTilePane(JSONObject gameData){
        JSONObject currentTileData = gameData.getJSONObject("gameMatch").getJSONObject("currentTile");

        currentTileCtrl.setPlacedGraphics(
                currentTileData.getInt("top"),
                currentTileData.getInt("left"),
                currentTileData.getInt("right")
        );
    }

    String getFormattedPlayerStatusText(String playerState){
        //Todo: tidy
        if(playerState == "PLACING"){
            return txt_statusPlacing;
        }
        else if(playerState == "WAIT_OTHER"){
            return txt_statusWaitOther;
        }
        return "?";
    }

    void refreshPlayersList(JSONObject gameData){
        if(players == null){
            buildPlayersList(gameData);
        }

        JSONObject matchData = gameData.getJSONObject("gameMatch");
        JSONArray playersData = matchData.getJSONArray("players");
        for (int iii = 0; iii < playersData.length(); ++iii){
            JSONObject playerData = playersData.getJSONObject(iii);
            PlayerListEntryCtrl player = players.get(playerData.getString("playerName"));

            String statusText;
            if(matchData.getString("matchState") == "FINISH"){
                statusText = "Score: " + playerData.getInt("playerScore");
            }
            else{
                statusText = getFormattedPlayerStatusText(playerData.getString("playerState"));
            }
            player.setValues(statusText);

            //Todo: refactor: extract method?
            player.btn_focus.setDisable(playerData.getString("playerName").equals(focusedPlayerName));
            player.btn_showKickDialog.setDisable(players.size()<2 || matchData.getString("matchState") == "FINISH");
            player.pane_kickDialog.setVisible(false);
        }

    }

    void refreshPlacingButton(JSONObject gameData){

        JSONObject playerData = getPlayerDataFromPlayerName(
                gameData.getJSONObject("gameMatch").
                        getJSONArray("players"),
                focusedPlayerName
        );

        //todo: maybe add a graphic change on the text or background
        btn_placeTile.setDisable(focusedCoordinates == null || playerData.get("playerState") != "PLACING");
    }

    void refreshBoard(JSONObject gameData){
        if(focusedPlayerName == null){
            focusFirstPlacingPlayer(gameData);
        }

        JSONObject playerData = getPlayerDataFromPlayerName(
                gameData.getJSONObject("gameMatch")
                        .getJSONArray("players"),
                focusedPlayerName
        );

        JSONObject boardData = playerData.getJSONObject("playerBoard");
        JSONObject currentTileData = gameData.getJSONObject("gameMatch").getJSONObject("currentTile");

        for(HexCoordinates possibleCoord : tiles.keySet()){

            //TODO: refactor: extract method to make more clear what the function does
            //TODO: consider introducing function with specific "callback" name (es. on...release) that
            //      just calls defocusCoordinates/focusCoordinates

            if(boardData.opt(possibleCoord.toString()) != null){
                JSONObject tileData = boardData.getJSONObject(possibleCoord.toString());

                tiles.get(possibleCoord).setPlacedGraphics(tileData.getInt("top"), tileData.getInt("left"), tileData.getInt("right"));

                if(playerData.get("playerState") == "PLACING") {
                    tiles.get(possibleCoord).graphic_hitBox.setOnMouseReleased(e -> defocusCoordinates());
                }
                else{
                    tiles.get(possibleCoord).graphic_hitBox.setOnMouseReleased(e -> {});
                }
            }
            else{
                if(possibleCoord != focusedCoordinates){
                    tiles.get(possibleCoord).resetGraphics();
                }
                if(playerData.get("playerState") == "PLACING") {
                    tiles.get(possibleCoord).graphic_hitBox.setOnMouseReleased(e -> focusCoordinates(possibleCoord, currentTileData));
                }
                else{
                    tiles.get(possibleCoord).graphic_hitBox.setOnMouseReleased(e -> {});
                }
            }
        }
    }

    void refreshMatchInfo(JSONObject gameData){
        JSONObject matchData = gameData.getJSONObject("gameMatch");
        String matchStateText;
        JSONArray playersData = matchData.getJSONArray("players");

        if(matchData.get("matchState") == "PLAY"){
            int totalNumberOfPlayers = playersData.length();
            int placingPlayers = 0;

            //TODO: refactor with streams (probably count())
            for(int iii = 0; iii < playersData.length(); ++iii){
                if(playersData.getJSONObject(iii).get("playerState") == "PLACING"){
                    placingPlayers++;
                }
            }
            matchStateText = placingPlayers + " player"+
                    (placingPlayers>1?"s":"") +
                    " out of " + totalNumberOfPlayers + (placingPlayers>1?" are":" is") + " still placing.";
        }
        // else match is finished
        else{
            ArrayList<String> winners = computeHighestScoringPlayerNames(gameData);

            if(winners.size()>1){
                matchStateText = "TIE!";
            }
            else{
                int bestScore = getPlayerDataFromPlayerName(playersData, winners.get(0)).getInt("playerScore");
                matchStateText = "The winner is " + winners.get(0) +
                        " with a score of "+ bestScore + "!";
            }
        }

        text_matchStatus.setText(matchStateText);
    }

    void refreshCurrentPlayerInfo(JSONObject gameData) {

        text_playerName.setText(focusedPlayerName);

        String focusedPlayerState = getPlayerDataFromPlayerName(
                    gameData.getJSONObject("gameMatch")
                            .getJSONArray("players"),
                    focusedPlayerName
            ).getString("playerState");

        text_playerStatus.setText(getFormattedPlayerStatusText(focusedPlayerState));
    }

    void refreshRematchPanel(JSONObject gameData){
        String matchState = gameData.getJSONObject("gameMatch").getString("matchState");
        pane_rematchPanel.setVisible(matchState == "FINISH");
    }

    @Override
    public void refreshView(JSONObject gameData) {

        //Todo: remove, DEBUG
//        System.out.println(gameData);

        refreshBoard(gameData);
        refreshCurrentTilePane(gameData);
        refreshPlayersList(gameData);
        refreshPlacingButton(gameData);
        refreshRematchPanel(gameData);
        refreshCurrentPlayerInfo(gameData);
        refreshMatchInfo(gameData);

    }
}
