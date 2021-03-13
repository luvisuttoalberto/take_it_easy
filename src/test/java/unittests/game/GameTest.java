package unittests.game;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import takeiteasy.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testEmptyDataBeforeCreatingLocalLobby(){
        Game game = new Game();
        assertNull(game.getData());
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

    
}
