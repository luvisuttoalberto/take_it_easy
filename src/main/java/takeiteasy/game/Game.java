package takeiteasy.game;

import org.json.JSONObject;
import takeiteasy.board.HexCoordinates;
import takeiteasy.board.IBoard;
import takeiteasy.board.OutOfBoardCoordinatesException;
import takeiteasy.gamematch.*;
import takeiteasy.player.InvalidPlayerStateException;
import takeiteasy.tilepool.Tile;

public class Game implements IGame{
    private GameMatch gameMatch;
    private String message = "";
    private State state = State.MAIN_MENU; // TODO: Do we need a ctor to initialize this variable

    @Override
    public void createLocalGame() {
        gameMatch = new GameMatch();
        state = State.LOCAL_LOBBY;
    }

    @Override
    public void addPlayer(String name) {
        try {
            gameMatch.addPlayer(name);
        }
        catch (PlayersWithSameNameNotAllowedException e) {
            message = "Player not added, a player with this name is already present";
        }
        catch (InvalidMatchStateException ignored){
        }
    }

    @Override
    public void removePlayer(String name) {
        try{
            gameMatch.removePlayer(name);
        }
        catch (PlayerNameNotFoundException ignored){
        }
        catch (NotEnoughPlayersException ignored){
            //TODO: maybe display a message on the JsonObject
        }
    }

    @Override
    public void renamePlayer(String oldName, String newName) {
        try{
            gameMatch.setPlayerName(oldName, newName);
        }
        catch(PlayerNameNotFoundException | InvalidMatchStateException ignored){
        }
        catch(PlayersWithSameNameNotAllowedException e){
            message = "Player name not changed, a player with this name is already present";
        }
    }

    @Override
    public void setMatchSeed(long seed) {
        try {
            gameMatch.setTilePoolSeed(seed);
        }
        catch (InvalidMatchStateException ignored){
        }
    }

    @Override
    public void startLocalMatch() {
        try {
            gameMatch.startMatch();
            state = State.LOCAL_MATCH;
        }
        catch(InvalidMatchStateException | NotEnoughPlayersException | InvalidPlayerStateException ignored){
        }
    }

    @Override
    public void backToTheMainMenu() {

    }

    @Override
    public void playerPlacesTileAt(String name, HexCoordinates coordinates) {

    }

    @Override
    public void backToLocalSetup() {

    }

    @Override
    public JSONObject getData() {
        if(state == State.MAIN_MENU){
            //TODO: Verify if this is a good practice: we do not want to throw an exception to GUI
            //      this method should not be called in the MAIN MENU
            return null;
        }
        JSONObject data = new JSONObject();
        if(!message.isBlank()){
            data.put("message", message);
            message = "";
        }
        JSONObject playersData = new JSONObject();
        String[] playerNames = gameMatch.getPlayerNames();
        JSONObject playerData = new JSONObject();
        for( int i = 0; i < playerNames.length; ++i ){
            try {
                String playerState = gameMatch.getPlayerStateFromPlayerName(playerNames[i]).name();
                playerData.put("playerState", playerState);
            }
            catch(PlayerNameNotFoundException ignored){
            }
            JSONObject boardData = new JSONObject();
            //TODO: remove duplication generateCoordinateSequence54 -> generateStandardCoordinateSequence
            int[][] coordinateSet = {
                    {-2, 2, 0}, {-2, 1, 1}, {-2, 0, 2},
                    {-1, 2, -1}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                    {0, 2, -2}, {0, 1, -1}, {0, 0, 0}, {0, -1, 1}, {0, -2, 2},
                    {1, 1, -2}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                    {2, 0, -2}, {2, -1, -1}, {2, -2, 0}
            };
            HexCoordinates[] coords = new HexCoordinates[19];
            try{
                for (int j = 0; j < 19; ++j){
                    coords[j] = new HexCoordinates(coordinateSet[j][0],coordinateSet[j][1],coordinateSet[j][2]);
                }
            } catch(Exception ignored){
            }

            try {
                IBoard playerBoard = gameMatch.getBoardFromPlayerName(playerNames[i]);
                for( HexCoordinates c : coords ){
                    Tile tile = playerBoard.getTile(c);
                    //if(!tile.equals(null)){
                    if(tile != null){
                        JSONObject tileData = new JSONObject();
                        tileData.put("top",tile.getTop());
                        tileData.put("left",tile.getLeft());
                        tileData.put("right",tile.getRight());
                        boardData.put(c.getX() + " " + c.getY() + " " + c.getZ() , tileData);
                    }
                }
            }
            catch(PlayerNameNotFoundException | OutOfBoardCoordinatesException ignored){
            }
            playerData.put("playerBoard", boardData);
            playersData.put(playerNames[i], playerData);
        }
        data.put("players", playersData);

        JSONObject currentTileData = new JSONObject();
        Tile currentTile = gameMatch.getCurrentTile();
        currentTileData.put("top", currentTile.getTop());
        currentTileData.put("left", currentTile.getLeft());
        currentTileData.put("right", currentTile.getRight());
        data.put("currentTile", currentTileData);

        data.put("gameState", state.name());
        try{
            JSONObject scoresData = new JSONObject(gameMatch.computeScore());
            data.put("scores", scoresData);
        }
        catch (InvalidMatchStateException ignored){
        }
        return data;
    }
}
