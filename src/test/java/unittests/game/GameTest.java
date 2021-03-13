package unittests.game;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import takeiteasy.game.Game;

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
        assertNull(data.opt("message"));
        assertEquals(data.get("gameState"), "LOCAL_LOBBY");
    }

}
