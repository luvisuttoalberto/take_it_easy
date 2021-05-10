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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoard();
        buildCurrentTilePane();
        btn_backToLobby.setOnMouseReleased(e -> onBackToLobbyRelease());
        btn_backToMenu.setOnMouseReleased(e -> onBackToMenuRelease());

        btn_rematch.setText("Rematch\n(SAME tile pool)");
        btn_rematchNewSeed.setText("Rematch\n(new tile pool)");
        btn_rematch.setOnMouseReleased(e -> onRematchRelease());
        btn_rematchNewSeed.setOnMouseReleased(e -> onRematchNewSeedRelease());
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

            pane_boardPane.getChildren().add(t);
            tiles.put(coords, t);
        }

        //todo: Move this to linkui()? If so, we have to call it somewhere
        btn_placeTile.setOnMouseReleased(e -> onPlaceTileRelease());
    }

    void buildCurrentTilePane() {
        //Todo: maybe change these values
        double currentTileWidth = 80;

        double currentTileHeight = currentTileWidth*.5*1.732;

        currentTileCtrl = new TileCtrl(currentTileWidth, currentTileHeight);

        pane_currentTile.getChildren().add(currentTileCtrl);

    }

    void buildPlayersList(JSONObject gameData){
        //TODO: should this be converted to an array list, since it is constructed as the JSONArray?
        players = new HashMap<>();

        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        for (int i = 0; i < playersData.length(); ++i){

            String playerName = playersData.getJSONObject(i).getString("playerName");
            PlayerListEntryCtrl pe = new PlayerListEntryCtrl(playerName);

            players.put(playerName, pe);
            pe.btn_focus.setOnMouseReleased(e -> onFocusPlayerRelease(playerName));
            pe.btn_kick_confirm.setOnMouseReleased(e -> onKickPlayerRelease(playerName));
            layout_playersPane.getChildren().add(pe);
        }
    }

    void focusNextPlacingPlayer(JSONObject gameData){

        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        for(int i = 0; i < playersData.length(); ++i){
            if(playersData.getJSONObject(i).get("playerState").equals("PLACING")){
                focusedPlayerName = playersData.getJSONObject(i).getString("playerName");
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
        JSONArray playersData = gameData.getJSONObject("gameMatch").
                getJSONArray("players");

        ArrayList<String> winners = new ArrayList<>();
        int bestScore = 0;
        for(int i = 0; i < playersData.length(); ++i){
            int currentPlayerScore = playersData.getJSONObject(i).
                    getInt("playerScore");
            if(currentPlayerScore > bestScore){
                bestScore = currentPlayerScore;
                winners.clear();
                winners.add(playersData.getJSONObject(i).getString("playerName"));
            }
            else if(currentPlayerScore == bestScore){
                winners.add(playersData.getJSONObject(i).getString("playerName"));
            }
        }
        return winners;
    }

    void onFocusPlayerRelease(String playerName){
        focusedPlayerName = playerName;
        refreshView(game.getData());
    }

    void onKickPlayerRelease(String playerName){
        game.removePlayer(playerName);

        layout_playersPane.getChildren().remove(players.get(playerName));
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

    void refreshCurrentTilePane(JSONObject gameData){
        JSONObject currentTileData = gameData.getJSONObject("gameMatch").getJSONObject("currentTile");

        //Todo: create specific graphics function
        currentTileCtrl.setPlacedGraphics(
                currentTileData.getInt("top"),
                currentTileData.getInt("left"),
                currentTileData.getInt("right")
        );
    }

    void refreshPlayersList(JSONObject gameData){
        JSONObject matchData = gameData.getJSONObject("gameMatch");
        JSONArray playersData = matchData.getJSONArray("players");
        for (int i = 0; i < playersData.length(); ++i){
            JSONObject playerData = playersData.getJSONObject(i);
            PlayerListEntryCtrl player = players.get(playerData.getString("playerName"));

            String statusText;
            if(matchData.getString("matchState") == "FINISH"){
                statusText = String.valueOf(playerData.getInt("playerScore"));
            }
            else{
                statusText = playerData.getString("playerState");
            }
            player.setValues(statusText);

            player.btn_focus.setDisable(playerData.getString("playerName") == focusedPlayerName);
            player.btn_showKickDialog.setDisable(players.size()<2 || matchData.getString("matchState") == "FINISH");
            player.pane_kickDialog.setVisible(false);
        }

    }

    void refreshPlacingButton(JSONObject gameData){

        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        Integer index = 0;
        for(int i = 0; i < playersData.length(); ++i){
            if(focusedPlayerName == playersData.getJSONObject(i).getString("playerName")){
                index = i;
            }
        }
        JSONObject playerData = playersData.getJSONObject(index);

        //todo: magic fields, maybe add a graphic change on the text or background
        if(focusedCoordinates != null && playerData.get("playerState") == "PLACING"){
            btn_placeTile.setDisable(false);
        }
        else{
            btn_placeTile.setDisable(true);
        }

    }

    void refreshBoard(JSONObject gameData){
        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        Integer index = 0;
        for(int i = 0; i < playersData.length(); ++i){
            if(focusedPlayerName == playersData.getJSONObject(i).getString("playerName")){
                index = i;
            }
        }

        JSONObject playerData = gameData.getJSONObject("gameMatch").getJSONArray("players").getJSONObject(index);
        JSONObject boardData = playerData.getJSONObject("playerBoard");
        JSONObject currentTileData = gameData.getJSONObject("gameMatch").getJSONObject("currentTile");

        for(HexCoordinates possibleCoord : tiles.keySet()){

            if(boardData.opt(possibleCoord.toString()) != null){
                JSONObject tileData = boardData.getJSONObject(possibleCoord.toString());

                tiles.get(possibleCoord).setPlacedGraphics(tileData.getInt("top"), tileData.getInt("left"), tileData.getInt("right"));

                if(playerData.get("playerState") == "PLACING") {
                    tiles.get(possibleCoord).graphic_hitBox.setOnMouseReleased(e -> focusCoordinates(null, null));
                }
                else{
                    tiles.get(possibleCoord).graphic_hitBox.setOnMouseReleased(e -> {});
                }
            }
            else{
                tiles.get(possibleCoord).resetGraphics();
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
            for(int i = 0; i < playersData.length(); ++i){
                if(playersData.getJSONObject(i).get("playerState") == "PLACING"){
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
                Integer winnerIndex = 0;
                for(int i = 0; i < playersData.length(); ++i){
                    if(winners.get(0) == playersData.getJSONObject(i).getString("playerName")){
                        winnerIndex = i;
                    }
                }
                int bestScore = gameData.getJSONObject("gameMatch").
                        getJSONArray("players").
                        getJSONObject(winnerIndex).
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
        JSONArray playersData = gameData.getJSONObject("gameMatch").getJSONArray("players");
        Integer index = 0;
        for(int i = 0; i < playersData.length(); ++i){
            if(focusedPlayerName == playersData.getJSONObject(i).getString("playerName")){
                index = i;
            }
        }

        JSONObject focusedPlayerData = gameData.getJSONObject("gameMatch").
                getJSONArray("players").
                getJSONObject(index);

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
    void refreshRematchPanel(JSONObject gameData){
        String matchState = gameData.getJSONObject("gameMatch").getString("matchState");
        //TODO: refactor state label
        pane_rematchPanel.setVisible(matchState == "FINISH");
    }

    @Override
    public void refreshView(JSONObject gameData) {

        //TODO: method for first refresh instead of these ifs?

        if(focusedPlayerName == null){
            focusNextPlacingPlayer(gameData);
        }

        if(players == null){
            buildPlayersList(gameData);
        }

        //Todo: remove
        System.out.println(gameData);

        //TODO: regroup these refresh functions? (eg board, currenttile, placingbtn, rematch)
        refreshBoard(gameData);
        refreshCurrentTilePane(gameData);
        refreshPlayersList(gameData);
        refreshPlacingButton(gameData);
        refreshRematchPanel(gameData);
        refreshCurrentPlayerInfo(gameData);
        refreshMatchInfo(gameData);

    }

    void linkUI(){
        //TODO:
    }

}
