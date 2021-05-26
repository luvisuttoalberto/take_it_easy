package takeiteasy.GUI.Controller;

import javafx.fxml.FXML;
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
import takeiteasy.GUI.Controller.LocalMatchComponents.*;
import takeiteasy.GUI.IViewUpdater;
import takeiteasy.core.JSONKeys;
import takeiteasy.core.board.HexCoordinates;
import takeiteasy.core.game.IGame;
import takeiteasy.core.gamematch.IGameMatch;
import takeiteasy.core.player.IPlayer;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static takeiteasy.utility.Utility.generateCoordinateStandard;


public class LocalMatchCtrl extends GridPane implements IViewController, Initializable {

    @FXML Pane pane_boardPane;

    @FXML Button btn_placeTile;

    @FXML Text text_playerName;
    @FXML Text text_playerStatus;
    @FXML Text text_matchStatus;

    @FXML ScrollPane pane_playersPane;
    @FXML VBox layout_playersPane;
    @FXML Pane pane_currentTile;
    @FXML Button btn_backToLobby;
    @FXML Button btn_backToMenu;
    @FXML Button btn_rematch;
    @FXML Button btn_rematchNewSeed;
    @FXML HBox pane_rematchPanel;

    IGame game;
    IViewUpdater vu;
    HexCoordinates focusedCoordinates;
    String focusedPlayerName;
    Map<HexCoordinates, TileCtrl> tiles;
    Map<String, PlayerListEntryCtrl> players;
    TileCtrl currentTileCtrl;

    final double TILE_WIDTH = 80;
    final double TILE_HEIGHT = TILE_WIDTH *.5*1.732;
    final double BOARD_X_UNIT = TILE_WIDTH *.75;
    final double BOARD_Y_UNIT = TILE_HEIGHT;

    final String PLAYER_STATUS_TEXT_PLACING = "Placing";
    final String PLAYER_STATUS_TEXT_WAIT_OTHER = "Waiting";
    final String PLAYER_STATUS_TEXT_WAIT_MATCH = "Waiting for new match";

    @Override
    public void injectGame(IGame g) {
        this.game = g;
    }

    @Override
    public void injectViewUpdater(IViewUpdater vu) {
        this.vu = vu;
    }

    void buildGraphicTileFromHexCoordinates(HexCoordinates coordinates){
        TileCtrl t = new TileCtrl(TILE_WIDTH, TILE_HEIGHT);

        double x = coordinates.getX() + 2;
        double y = 2 - (coordinates.getY() + coordinates.getX() * .5);

        t.setLayoutX(BOARD_X_UNIT * x);
        t.setLayoutY(BOARD_Y_UNIT * y);
        pane_boardPane.getChildren().add(t);
        tiles.put(coordinates, t);
    }

    void buildBoard(){
        tiles = new HashMap<>();
        Arrays.stream(generateCoordinateStandard()).forEach(this::buildGraphicTileFromHexCoordinates);
    }

    void buildCurrentTilePane() {
        currentTileCtrl = new TileCtrl(TILE_WIDTH, TILE_HEIGHT);
        pane_currentTile.getChildren().add(currentTileCtrl);
    }

    void linkStaticButtons(){
        btn_placeTile.setOnMouseReleased(e -> onPlaceTileRelease());
        btn_backToLobby.setOnMouseReleased(e -> onBackToLobbyRelease());
        btn_backToMenu.setOnMouseReleased(e -> onBackToMenuRelease());
        btn_rematch.setOnMouseReleased(e -> onRematchRelease());
        btn_rematchNewSeed.setOnMouseReleased(e -> onRematchNewSeedRelease());
    }

    void setRematchButtonsText(){
        btn_rematch.setText("Rematch\n(SAME tile pool)");
        btn_rematchNewSeed.setText("Rematch\n(new tile pool)");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        buildBoard();
        buildCurrentTilePane();
        linkStaticButtons();
        setRematchButtonsText();
    }

    void buildPlayerListEntry(String playerName){
        PlayerListEntryCtrl pe = new PlayerListEntryCtrl(playerName);
        players.put(playerName, pe);
        pe.btn_focus.setOnMouseReleased(e -> onFocusPlayerRelease(playerName));
        pe.btn_kick_confirm.setOnMouseReleased(e -> onKickPlayerRelease(playerName));
        layout_playersPane.getChildren().add(pe);
    }

    void buildPlayersList(JSONObject gameData){
        players = new HashMap<>();

        JSONArray playersData = gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONArray(JSONKeys.MATCH_PLAYERS);

        IntStream.range(0, playersData.length())
                .mapToObj(iii -> playersData.getJSONObject(iii).getString(JSONKeys.PLAYER_NAME))
                .forEach(this::buildPlayerListEntry);
    }

    void defocusCoordinates(){
        if (focusedCoordinates != null) {
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
                currentTileData.getInt(JSONKeys.TILE_TOP),
                currentTileData.getInt(JSONKeys.TILE_LEFT),
                currentTileData.getInt(JSONKeys.TILE_RIGHT)
            );
        }

        btn_placeTile.setDisable(false);
    }

    void focusPlayerAndDefocusCoordinates(String playerName){
        focusedPlayerName = playerName;
        defocusCoordinates();
    }

    void focusFirstPlacingPlayer(JSONObject gameData){
        JSONArray playersData = gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONArray(JSONKeys.MATCH_PLAYERS);
        IntStream.range(0, playersData.length())
                .mapToObj(playersData::getJSONObject)
                .filter(playerData -> playerData.get(JSONKeys.PLAYER_STATE).equals(IPlayer.State.PLACING.name()))
                .findFirst()
                .ifPresent(playerData -> focusPlayerAndDefocusCoordinates(playerData.getString(JSONKeys.PLAYER_NAME)));
    }

    JSONObject retrievePlayerDataFromPlayerName(JSONArray playersData, String playerName){
        int playerIndex = IntStream.range(0, playersData.length())
                .filter(iii -> playersData.getJSONObject(iii).getString(JSONKeys.PLAYER_NAME).equals(playerName))
                .findFirst()
                .getAsInt();

        return playersData.getJSONObject(playerIndex);
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

    Boolean isGameMatchInState(IGameMatch.State state, JSONObject gameData){
        return gameData
                .getJSONObject(JSONKeys.GAME_MATCH)
                .getString(JSONKeys.MATCH_STATE)
                .equals(state.name());
    }

    int computeHighestScore(JSONArray playersData){
        return IntStream.range(0, playersData.length())
                .map(iii -> playersData.getJSONObject(iii).getInt(JSONKeys.PLAYER_SCORE))
                .max().getAsInt();
    }

    List<String> computeHighestScoringPlayerNames(JSONObject gameData){
        JSONArray playersData = gameData.getJSONObject(JSONKeys.GAME_MATCH)
                .getJSONArray(JSONKeys.MATCH_PLAYERS);

        int bestScore = computeHighestScore(playersData);

        return IntStream.range(0, playersData.length())
                .mapToObj(playersData::getJSONObject)
                .filter(playerData -> playerData.getInt(JSONKeys.PLAYER_SCORE) == bestScore)
                .map(playerData -> playerData.getString(JSONKeys.PLAYER_NAME))
                .collect(Collectors.toList());
    }

    void onPlaceTileRelease(){
        game.playerPlacesTileAt(focusedPlayerName, focusedCoordinates);

        defocusCoordinates();

        JSONObject gameData = game.getData();

        if(isGameMatchInState(IGameMatch.State.FINISH, gameData)){
            focusPlayerAndDefocusCoordinates(computeHighestScoringPlayerNames(gameData).get(0));
        }
        else {
            focusFirstPlacingPlayer(gameData);
        }
        refreshView(gameData);
    }

    void onBackToLobbyRelease(){
        game.backToLocalLobby();
        game.setMatchSeed(new Random().nextLong());
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
        JSONObject currentTileData = gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONObject(JSONKeys.MATCH_CURRENT_TILE);

        currentTileCtrl.setPlacedGraphics(
                currentTileData.getInt(JSONKeys.TILE_TOP),
                currentTileData.getInt(JSONKeys.TILE_LEFT),
                currentTileData.getInt(JSONKeys.TILE_RIGHT)
        );
    }

    String computeFormattedPlayerStatusText(String playerState){
        if(playerState.equals(IPlayer.State.PLACING.name())){
            return PLAYER_STATUS_TEXT_PLACING;
        }
        else if(playerState.equals(IPlayer.State.WAIT_OTHER.name())){
            return PLAYER_STATUS_TEXT_WAIT_OTHER;
        }
        return PLAYER_STATUS_TEXT_WAIT_MATCH;
    }

    void refreshPlayerListEntryStatusText(String playerName, JSONObject gameData){
        PlayerListEntryCtrl playerCtrl = players.get(playerName);
        JSONObject playerData = retrievePlayerDataFromPlayerName(
                gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONArray(JSONKeys.MATCH_PLAYERS),
                playerName);

        String statusText;
        if(isGameMatchInState(IGameMatch.State.FINISH, gameData)){
            statusText = "Score: " + playerData.getInt(JSONKeys.PLAYER_SCORE);
        }
        else{
            statusText = computeFormattedPlayerStatusText(playerData.getString(JSONKeys.PLAYER_STATE));
        }
        playerCtrl.setValues(statusText);
    }

    void refreshPlayerListEntryControls(String playerName,JSONObject gameData){
        PlayerListEntryCtrl playerCtrl = players.get(playerName);

        playerCtrl.btn_focus.setDisable(playerName.equals(focusedPlayerName));
        playerCtrl.btn_showKickDialog.setDisable(players.size()<2 || isGameMatchInState(IGameMatch.State.FINISH,gameData));
        playerCtrl.pane_kickDialog.setVisible(false);
    }

    void refreshPlayersList(JSONObject gameData){
        if(players == null){
            buildPlayersList(gameData);
        }

        JSONArray playersData = gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONArray(JSONKeys.MATCH_PLAYERS);

        IntStream.range(0, playersData.length())
                .mapToObj(playersData::getJSONObject)
                .map(playerData -> playerData.getString(JSONKeys.PLAYER_NAME))
                .forEach(playerName -> {
                    refreshPlayerListEntryStatusText(playerName, gameData);
                    refreshPlayerListEntryControls(playerName, gameData);
                });
    }

    void refreshPlacingButton(JSONObject gameData){

        JSONObject playerData = retrievePlayerDataFromPlayerName(
                gameData.getJSONObject(JSONKeys.GAME_MATCH)
                        .getJSONArray(JSONKeys.MATCH_PLAYERS),
                focusedPlayerName
        );

        btn_placeTile.setDisable(
                focusedCoordinates == null ||
                !playerData.getString(JSONKeys.PLAYER_STATE).equals(IPlayer.State.PLACING.name()));
    }

    Boolean isThereATileAtCoordinates(HexCoordinates coordinates, JSONObject boardData){
        return boardData.opt(coordinates.toString()) != null;
    }

    void refreshTileGraphics(JSONObject boardData, HexCoordinates coordinates){
        if(isThereATileAtCoordinates(coordinates, boardData)){
            JSONObject tileData = boardData.getJSONObject(coordinates.toString());

            tiles.get(coordinates).setPlacedGraphics(
                    tileData.getInt(JSONKeys.TILE_TOP),
                    tileData.getInt(JSONKeys.TILE_LEFT),
                    tileData.getInt(JSONKeys.TILE_RIGHT)
            );
        }
        else{
            if (coordinates != focusedCoordinates) {
                tiles.get(coordinates).resetGraphics();
            }
        }
    }

    void refreshTileCallbackFunction(JSONObject playerData, HexCoordinates coordinates){
        if (playerData.getString(JSONKeys.PLAYER_STATE).equals(IPlayer.State.PLACING.name())) {

            JSONObject boardData = playerData.getJSONObject(JSONKeys.PLAYER_BOARD);

            if(isThereATileAtCoordinates(coordinates, boardData)){
                tiles.get(coordinates).graphic_hitBox.setOnMouseReleased(e -> defocusCoordinates());
            }
            else{
                JSONObject currentTileData = game.getData()
                        .getJSONObject(JSONKeys.GAME_MATCH)
                        .getJSONObject(JSONKeys.MATCH_CURRENT_TILE);

                tiles.get(coordinates).graphic_hitBox.setOnMouseReleased(e -> focusCoordinates(coordinates, currentTileData));
            }
        }
        else{
            tiles.get(coordinates).graphic_hitBox.setOnMouseReleased(e -> {});
        }
    }

    void refreshBoard(JSONObject gameData){
        if(focusedPlayerName == null){
            focusFirstPlacingPlayer(gameData);
        }

        JSONObject playerData = retrievePlayerDataFromPlayerName(
                gameData.getJSONObject(JSONKeys.GAME_MATCH)
                        .getJSONArray(JSONKeys.MATCH_PLAYERS),
                focusedPlayerName
        );

        JSONObject boardData = playerData.getJSONObject(JSONKeys.PLAYER_BOARD);

        tiles.keySet().forEach(possibleCoord -> {
            refreshTileGraphics(boardData, possibleCoord);
            refreshTileCallbackFunction(playerData, possibleCoord);
        });
    }

    int computeNumberOfPlacingPlayers(JSONArray playersData){
        return (int) IntStream.range(0, playersData.length())
                .filter(iii -> playersData.getJSONObject(iii).getString(JSONKeys.PLAYER_STATE)
                        .equals(IPlayer.State.PLACING.name()))
                .count();
    }

    String computeMatchInfoTextDuringPlay(int totalPlayers, int placingPlayers){
        return placingPlayers + " player"+ (placingPlayers>1?"s":"") +
                " out of " + totalPlayers + (placingPlayers>1?" are":" is") + " still placing.";
    }

    String computeMatchInfoTextDuringFinish(List<String> winners, int bestScore){
        String matchStateText ="";
        //NOTE: winners always has size()>0
        if(winners.size()>2){
            matchStateText += winners.size() + " players tied for first place";
        }
        else if(winners.size()==2){
            matchStateText += winners.get(0) + " and " + winners.get(1) + " tied for first place";
        }
        else{
            matchStateText = "The winner is " + winners.get(0);
        }

        return matchStateText + " with a score of "+ bestScore + "!";
    }

    void refreshMatchInfo(JSONObject gameData){

        JSONArray playersData = gameData.getJSONObject(JSONKeys.GAME_MATCH).getJSONArray(JSONKeys.MATCH_PLAYERS);

        String matchStateText;
        //NOTE: Match is either in PLAY or FINISH
        if(isGameMatchInState(IGameMatch.State.PLAY, gameData)){
            matchStateText = computeMatchInfoTextDuringPlay(
                    playersData.length(),
                    computeNumberOfPlacingPlayers(playersData));
        }
        else{
            matchStateText = computeMatchInfoTextDuringFinish(
                    computeHighestScoringPlayerNames(gameData),
                    computeHighestScore(playersData));
        }

        text_matchStatus.setText(matchStateText);
    }

    void refreshCurrentPlayerInfo(JSONObject gameData) {

        text_playerName.setText(focusedPlayerName);

        String focusedPlayerState = retrievePlayerDataFromPlayerName(
                    gameData.getJSONObject(JSONKeys.GAME_MATCH)
                            .getJSONArray(JSONKeys.MATCH_PLAYERS),
                    focusedPlayerName
            ).getString(JSONKeys.PLAYER_STATE);

        text_playerStatus.setText(computeFormattedPlayerStatusText(focusedPlayerState));
    }

    void refreshRematchPanel(JSONObject gameData){
        pane_rematchPanel.setVisible(isGameMatchInState(IGameMatch.State.FINISH,gameData));
    }

    @Override
    public void refreshView(JSONObject gameData) {

        refreshBoard(gameData);
        refreshCurrentTilePane(gameData);
        refreshPlayersList(gameData);
        refreshPlacingButton(gameData);
        refreshRematchPanel(gameData);
        refreshCurrentPlayerInfo(gameData);
        refreshMatchInfo(gameData);

    }
}
