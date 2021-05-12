package unittests.game;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import takeiteasy.board.BadHexCoordinatesException;
import takeiteasy.board.HexCoordinates;
import takeiteasy.game.Game;
import takeiteasy.tilepool.Tile;
import unittests.utility.Pair;

import static unittests.utility.Utility.getTilesAndCoordinatesBoard11;

import java.util.ArrayList;

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
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");

        assertEquals("LOCAL_LOBBY", data.get("gameState"));
        assertTrue(players.isEmpty());
    }

    @Test
    public void testAddPlayer(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Dario";
        game.addPlayer(name);

        JSONObject data = game.getData();
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");

        assertTrue(players.toString().contains(name));
        //TODO: maybe the following line might be better
        //        assertTrue(players.toString().contains("\"playerName\":\""+name+"\""));
    }

    @Test
    public void testCreateLocalLobbyDuringLocalLobby(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Carlos";
        game.addPlayer(name);
        game.createLocalLobby();
        JSONObject data = game.getData();
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");

        assertFalse(players.isEmpty());
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
    public void testAddAlreadyPresentPlayer(){
        Game game = new Game();
        game.createLocalLobby();
        String name = "Dario";
        game.addPlayer(name);
        game.addPlayer(name);

        JSONObject data = game.getData();
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");

        assertEquals(1, players.length());
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
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");
//        List<String> playerNames = new ArrayList<>(players.keySet());

        assertEquals(2, players.length());

        game.removePlayer(name);

        data = game.getData();
        players = data.getJSONObject("gameMatch").getJSONArray("players");
//        playerNames = new ArrayList<>(players.keySet());

        assertEquals(1, players.length());
        assertTrue(players.toString().contains(otherName));
        assertFalse(players.toString().contains(name));
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
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");

        assertEquals(newName, players.getJSONObject(0).get("playerName"));
        assertEquals(1, players.length());
        assertTrue(players.toString().contains(newName));
        assertFalse(players.toString().contains(oldName));
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
        JSONArray players = data.getJSONObject("gameMatch").getJSONArray("players");

        assertEquals(name, players.getJSONObject(0).get("playerName"));
        assertEquals(otherName, players.getJSONObject(1).get("playerName"));
        assertEquals(2, players.length());
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
        JSONArray players = matchData.getJSONArray("players");
        JSONObject player = players.getJSONObject(0);
        JSONObject board = player.getJSONObject("playerBoard");

        assertTrue(board.isEmpty());
        assertEquals("LOCAL_LOBBY", data.get("gameState"));
    }
}
