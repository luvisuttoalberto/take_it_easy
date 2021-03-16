package takeiteasy.game;

import org.json.JSONObject;
import takeiteasy.board.*;
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
        try {
            gameMatch.endMatch();
            state = State.MAIN_MENU;
        }
        catch(PlayersNotReadyToEndMatchException | InvalidMatchStateException | TilePoolNotDepletedException ignored) {
        }
    }

    @Override
    public void playerPlacesTileAt(String name, HexCoordinates coordinates) {
        try {
            gameMatch.positionCurrentTileOnPlayerBoard(name, coordinates);
            gameMatch.dealNextTile();
        }
        catch(PlayerNameNotFoundException | BadHexCoordinatesException | OutOfBoardCoordinatesException | InvalidPlayerStateException | CoordinatesOccupidedException | InvalidMatchStateException | NotEnoughPlayersException | PlayersNotReadyForNextTileException ignored){
        }
        catch(TilePoolDepletedException e){
            message = "Tilepool depleted";
        }
    }

    @Override
    public void backToLocalSetup() {
        try {
            gameMatch.backToSetup();
            state = State.LOCAL_LOBBY;
        }
        catch(InvalidMatchStateException ignored){
        }
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("gameState", state.name());
        if(state == State.MAIN_MENU){
             return data;
        }
        if(!message.isBlank()){
            data.put("message", message);
            message = ""; //TODO: for network implementation: this should be adapted, at least in the "Tilepool depleted" case
        }
        JSONObject playersData = new JSONObject();
        JSONObject playerData = new JSONObject();
        for (String playerName : gameMatch.getPlayerNames()) {
            try {
                String playerState = gameMatch.getPlayerStateFromPlayerName(playerName).name();
                playerData.put("playerState", playerState);
            } catch (PlayerNameNotFoundException ignored) {
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
            try {
                for (int j = 0; j < 19; ++j) {
                    coords[j] = new HexCoordinates(coordinateSet[j][0], coordinateSet[j][1], coordinateSet[j][2]);
                }
            } catch (Exception ignored) {
            }

            try {
                IBoard playerBoard = gameMatch.getBoardFromPlayerName(playerName);
                for (HexCoordinates c : coords) {
                    Tile tile = playerBoard.getTile(c);
                    if (tile != null) {
                        JSONObject tileData = new JSONObject();
                        tileData.put("top", tile.getTop());
                        tileData.put("left", tile.getLeft());
                        tileData.put("right", tile.getRight());
                        boardData.put(c.getX() + " " + c.getY() + " " + c.getZ(), tileData);
                    }
                }
            } catch (PlayerNameNotFoundException | OutOfBoardCoordinatesException ignored) {
            }
            playerData.put("playerBoard", boardData);
            playersData.put(playerName, playerData);
        }
        data.put("players", playersData);

        JSONObject currentTileData = new JSONObject();
        Tile currentTile = gameMatch.getCurrentTile();
        currentTileData.put("top", currentTile.getTop());
        currentTileData.put("left", currentTile.getLeft());
        currentTileData.put("right", currentTile.getRight());
        data.put("currentTile", currentTileData);

        data.put("seed", gameMatch.getSeed());
        try{
            JSONObject scoresData = new JSONObject(gameMatch.computeScore());
            data.put("scores", scoresData);
        }
        catch (InvalidMatchStateException ignored){
        }
        return data;
    }
}
