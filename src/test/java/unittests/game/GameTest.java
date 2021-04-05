package unittests.game;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import takeiteasy.board.BadHexCoordinatesException;
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
        assertEquals("LOCAL_LOBBY", data.get("gameState"));
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

        assertTrue(players.keySet().contains(name));
        assertEquals("WAIT_MATCH", players.getJSONObject(name).get("playerState"));
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

        assertEquals(1, players.keySet().size());
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

        long initialSeed = data.getLong("seed");
        long newSeed = initialSeed + 1;
        game.setMatchSeed(newSeed);
        data = game.getData();

        assertEquals(newSeed, data.getLong("seed"));
    }

    @Test
    public void testStartLocalMatch(){
        Game game = new Game();
        game.createLocalGame();
        game.addPlayer("Dario");
        game.startLocalMatch();

        assertEquals("LOCAL_MATCH", game.getData().get("gameState"));
    }

    //TODO: we are testing a behaviour of GameMatch, not Game!
    //      should we avoid this?
    @Test
    public void testEndMatch(){
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
        game.endMatch();

        JSONObject data = game.getData();

        assertEquals("FINISH", data.get("matchState"));
    }

    @Test
    public void testBackToMainMenu(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";

        try {
            HexCoordinates coord = new HexCoordinates(0, 0, 0);
            game.addPlayer(name);
            game.setMatchSeed(11);
            game.startLocalMatch();

            game.playerPlacesTileAt(name, coord);
        }
        catch (BadHexCoordinatesException ignored){
        }

        game.backToTheMainMenu();
        JSONObject data = game.getData();
        assertEquals("MAIN_MENU", data.get("gameState"));
    }

    //TODO: as before, is the whole test to be removed? the only thing we tested is if the
    //      tiles are all positioned in the right place, but this is done in the BoardVanillaTest
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
        game.endMatch();
        data = game.getData();
        JSONObject players = data.getJSONObject("players");
        JSONObject player = players.getJSONObject(name);
        JSONObject board = player.getJSONObject("playerBoard");
        //TODO: To remove, tested in BoardVanillaTest
        for (Pair<Tile, HexCoordinates> tilesAndCoord : tilesAndCoords) {
            JSONObject tileData = board.getJSONObject(tilesAndCoord.coordinate.toString());
            Tile tile = new Tile(tileData.getInt("top"), tileData.getInt("left"), tileData.getInt("right"));
            assertEquals(tilesAndCoord.tile, tile);
        }
    }

    @Test
    public void testBackToLocalSetup(){
        Game game = new Game();
        game.createLocalGame();
        String name = "Dario";

        try {
            HexCoordinates coord = new HexCoordinates(0, 0, 0);
            game.addPlayer(name);
            game.setMatchSeed(11);
            game.startLocalMatch();

            game.playerPlacesTileAt(name, coord);
        }
        catch (BadHexCoordinatesException ignored){
        }

        game.backToLocalSetup();

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("players");
        JSONObject player = players.getJSONObject(name);
        JSONObject board = player.getJSONObject("playerBoard");

        assertTrue(board.isEmpty());
        assertEquals("LOCAL_LOBBY", data.get("gameState"));
    }
}
