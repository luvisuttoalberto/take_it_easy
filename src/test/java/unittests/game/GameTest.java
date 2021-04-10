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
    public void testConsistentGetData(){
        Game game = new Game();
        assertNotNull(game.getData().opt("gameState"));
        assertNull(game.getData().opt("gameMatch"));
    }

    @Test
    public void testCreateLocalLobby(){
        Game game = new Game();
        assertEquals("MAIN_MENU", game.getData().get("gameState"));
        game.createLocalLobby();

        JSONObject data = game.getData();
        assertNotNull(data.opt("gameMatch"));
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");

        assertEquals("LOCAL_LOBBY", data.get("gameState"));
        assertTrue(players.keySet().isEmpty());
    }

    @Test
    public void testCreateLocalLobbyDuringLocalLobby(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Carlos";
        game.addPlayer(name);
        game.createLocalLobby();
        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");
        assertFalse(players.keySet().isEmpty());
    }

    @Test
    public void testCreateLocalLobbyInLocalMatch(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Carlos";
        game.addPlayer(name);
        game.startLocalMatch();
        game.createLocalLobby();
        JSONObject data = game.getData();
        assertEquals("LOCAL_MATCH", data.get("gameState"));
    }

    @Test
    public void testAddPlayer(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Dario";
        game.addPlayer(name);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");

        assertTrue(players.keySet().contains(name));
    }

    @Test
    public void testAddAlreadyPresentPlayer(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Dario";
        game.addPlayer(name);
        game.addPlayer(name);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");

        assertEquals(1, players.keySet().size());
    }

    @Test
    public void testRemovePlayer(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Dario";
        String otherName = "Carlos";
        game.addPlayer(name);
        game.addPlayer(otherName);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());

        assertEquals(2, playerNames.size());

        game.removePlayer(name);

        data = game.getData();
        players = data.getJSONObject("gameMatch").getJSONObject("players");
        playerNames = new ArrayList<>(players.keySet());

        assertEquals(1, playerNames.size());
        assertTrue(playerNames.contains(otherName));
        assertFalse(playerNames.contains(name));
    }

    @Test
    public void testRenamePlayer(){
        Game game = new Game();
        game.createLocalLobby();
        String oldName = "Dario";
        String newName = "Carlos";
        game.addPlayer(oldName);
        game.renamePlayer(oldName, newName);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());

        assertEquals(newName, playerNames.get(0));
        assertEquals(1, playerNames.size());
        assertTrue(playerNames.contains(newName));
        assertFalse(playerNames.contains(oldName));
    }

    @Test
    public void testRenamePlayerWithAlreadyPresentName(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Dario";
        String otherName = "Carlos";
        game.addPlayer(name);
        game.addPlayer(otherName);

        game.renamePlayer(name, otherName);

        JSONObject data = game.getData();
        JSONObject players = data.getJSONObject("gameMatch").getJSONObject("players");
        List<String> playerNames = new ArrayList<>(players.keySet());

        assertEquals(name, playerNames.get(0));
        assertEquals(otherName, playerNames.get(1));
        assertEquals(2, playerNames.size());
    }

    @Test
    public void testSetMatchSeed(){
        Game game = new Game();
        game.createLocalLobby();
        JSONObject data = game.getData();
        JSONObject matchData = data.getJSONObject("gameMatch");

        long initialSeed = matchData.getLong("seed");
        long newSeed = initialSeed + 1;
        game.setMatchSeed(newSeed);
        data = game.getData();
        matchData = data.getJSONObject("gameMatch");

        assertEquals(newSeed, matchData.getLong("seed"));
    }

    @Test
    public void testStartLocalMatch(){
        Game game = new Game();
        game.createLocalLobby();
        game.addPlayer("Dario");
        game.startLocalMatch();

        assertEquals("LOCAL_MATCH", game.getData().get("gameState"));
    }

    @Test
    public void testBackToMainMenu(){
        Game game = new Game();
        game.createLocalLobby();
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
        assertNull(game.getData().opt("gameMatch"));
    }

    @Test
    public void testPlayerPlacesTileAt(){
        Game game = new Game();
        game.createLocalLobby();
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
        JSONObject matchData = data.getJSONObject("gameMatch");
        assertEquals("FINISH", matchData.get("matchState"));
    }

    @Test
    public void testBackToLocalLobby(){
        Game game = new Game();
        game.createLocalLobby();
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

        game.backToLocalLobby();

        JSONObject data = game.getData();
        JSONObject matchData = data.getJSONObject("gameMatch");
        JSONObject players = matchData.getJSONObject("players");
        JSONObject player = players.getJSONObject(name);
        JSONObject board = player.getJSONObject("playerBoard");

        assertTrue(board.isEmpty());
        assertEquals("LOCAL_LOBBY", data.get("gameState"));
    }
}
