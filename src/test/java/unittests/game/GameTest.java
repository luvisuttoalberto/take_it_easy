package unittests.game;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.Game;
import takeiteasy.tilepool.Tile;
import unittests.utility.Pair;

import static unittests.utility.Utility.getTilesAndCoordinatesBoard11;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testGetDataInMainMenu(){
        Game game = new Game();
        assertEquals("MAIN_MENU", game.getData().get("gameState"));
    }

    @Test
    public void testCreateLocalLobby(){
        Game game = new Game();
        game.createLocalGame();
        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        assertNull(data.opt("message"));
        assertEquals(data.get("gameState"), "LOCAL_LOBBY");
        assertTrue(players.keySet().isEmpty());
    }

    @Test
    public void testAddPlayer(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        game.addPlayer(name);
        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());
        JSONObject firstPlayerData = players.getJSONObject(playerNames.get(0));

        assertTrue(playerNames.contains(name));
        assertEquals(firstPlayerData.get("playerState"), "WAIT_MATCH");
        assertNull(data.opt("message"));
    }

    @Test
    public void testAddAlreadyPresentPlayer(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        game.addPlayer(name);
        game.addPlayer(name);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());
        assertEquals(1, playerNames.size());
        assertEquals("Player not added, a player with this name is already present", data.opt("message"));
        data = game.getData();
        assertNull(data.opt("message"));
    }

    @Test
    public void testRemovePlayer(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        String otherName = "Carlos";
        game.addPlayer(name);
        game.addPlayer(otherName);
        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());
        assertEquals(2, playerNames.size());

        game.removePlayer(name);
        data = game.getData();
        players = data.getJSONObject("players");
        playerNames = new ArrayList<>(players.keySet());
        assertEquals(1, playerNames.size());
        assertTrue(playerNames.contains(otherName));
        assertFalse(playerNames.contains(name));
    }

    @Test
    public void testRenamePlayer(){
        Game game = new Game();
        game.createLocalGame();
        String oldName = "Dario";
        String newName = "Carlos";
        game.addPlayer(oldName);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());

        assertEquals(oldName, playerNames.get(0));

        game.renamePlayer(oldName, newName);

        data = game.getData();
        players = data.getJSONObject("players");
        playerNames = new ArrayList<>(players.keySet());

        assertEquals(newName, playerNames.get(0));
        assertEquals(1, playerNames.size());
        assertTrue(playerNames.contains(newName));
        assertFalse(playerNames.contains(oldName));
    }

    @Test
    public void testRenamePlayerWithAlreadyPresentName(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        String otherName = "Carlos";
        game.addPlayer(name);
        game.addPlayer(otherName);

        game.renamePlayer(name, otherName);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());

        assertEquals(name, playerNames.get(0));
        assertEquals(otherName, playerNames.get(1));
        assertEquals(2, playerNames.size());
        assertEquals("Player name not changed, a player with this name is already present", data.opt("message"));
    }

    @Test
    public void testSetMatchSeed(){
        Game game = new Game();
        game.createLocalGame();
        JSONObject data = game.getData();

        long initialSeed = (long) data.get("seed");
        long newSeed = initialSeed + 1;
        game.setMatchSeed(newSeed);
        data = game.getData();

        assertNotEquals(initialSeed, data.get("seed"));
        assertEquals(newSeed, data.get("seed"));
    }

    @Test
    public void testStartLocalMatch(){
        Game game = new Game();
        game.createLocalGame();
        game.addPlayer("Dario");
        game.startLocalMatch();
        JSONObject data = game.getData();
        assertEquals(data.get("gameState"), "LOCAL_MATCH");
    }

    @Test
    public void testBackToMainMenu(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);

        game.addPlayer(name);
        game.setMatchSeed(11);
        game.startLocalMatch();

        for (int i = 0; i < tilesAndCoords.size() - 1; ++i) {
            game.playerPlacesTileAt(name, tilesAndCoords.get(i).coordinate);
        }
        game.playerPlacesTileAt(name, tilesAndCoords.get(18).coordinate);
        game.backToTheMainMenu();
        JSONObject data = game.getData();
        assertEquals(data.get("gameState"), "MAIN_MENU");
    }

    @Test
    public void testPlayerPlacesTileAt(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);

        game.addPlayer(name);
        game.setMatchSeed(11);
        game.startLocalMatch();

        for (int i = 0; i < tilesAndCoords.size() - 1; ++i) {
            game.playerPlacesTileAt(name, tilesAndCoords.get(i).coordinate);
        }
        game.playerPlacesTileAt(name, tilesAndCoords.get(18).coordinate);

        JSONObject data = game.getData();
        assertEquals("Tilepool depleted", data.opt("message"));
        JSONObject players = data.getJSONObject("players");
        JSONObject player = players.getJSONObject(name);
        JSONObject board = player.getJSONObject("playerBoard");
        for (Pair<Tile, HexCoordinates> tilesAndCoord : tilesAndCoords) {
            JSONObject tile = board.getJSONObject(tilesAndCoord.coordinate.getX() + " " + tilesAndCoord.coordinate.getY() + " " + tilesAndCoord.coordinate.getZ());
            assertEquals(tilesAndCoord.tile.getTop(), tile.get("top"));
            assertEquals(tilesAndCoord.tile.getLeft(), tile.get("left"));
            assertEquals(tilesAndCoord.tile.getRight(), tile.get("right"));
        }
    }

    @Test
    public void testBackToLocalSetup(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";
        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);
        game.setMatchSeed(11);
        game.addPlayer(name);
        game.startLocalMatch();
        game.playerPlacesTileAt(name, tilesAndCoords.get(0).coordinate);
        game.backToLocalSetup();
        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        JSONObject player = players.getJSONObject(name);
        JSONObject board = player.getJSONObject("playerBoard");
        assertNull(board.opt(tilesAndCoords.get(1).coordinate.getX() + " " + tilesAndCoords.get(1).coordinate.getY() + " " + tilesAndCoords.get(1).coordinate.getZ()));
        assertEquals(data.get("gameState"), "LOCAL_LOBBY");
    }
}
