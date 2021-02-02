package unittests.gamematch;

import org.junit.jupiter.api.Test;
import takeiteasy.gamematch.*;
import takeiteasy.player.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GameMatchTest {

    @Test
    public void testAddPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        Player ply = new Player(plyName);
        try {
            gm.addPlayer(ply);
        }
        catch (Exception e){
            fail("player add failed");
        }

        assertTrue(Arrays.stream(gm.getPlayerNames()).anyMatch(plyName::equals));

    }
    @Test
    public void testAddDuplicatePlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        Player ply = new Player(plyName);
        try {
            gm.addPlayer(ply);
            gm.addPlayer(ply);
            fail();
        }
        catch (PlayerWithSameNameCannotBeAddedException ignored){
            // test passed
        }
    }

    @Test
    public void testRemovePlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        Player ply = new Player(plyName);
        try {
            gm.addPlayer(ply);
            gm.removePlayer(plyName);
        }
        catch (Exception e){
            fail();
        }
    }
    @Test
    public void testRemoveAbsentPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        try {
            gm.removePlayer(plyName);
            fail();
        }
        catch (PlayerNameNotFoundException ignored){
            // test pass
        }
    }

    @Test
    public void testSetPlayerName(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Karlos";
        Player ply = new Player(oldName);
        try{
            gm.addPlayer(ply);
            gm.setPlayerName(oldName, newName);
            assertTrue(Arrays.stream(gm.getPlayerNames()).anyMatch(newName::equals));
            assertFalse(Arrays.stream(gm.getPlayerNames()).anyMatch(oldName::equals));
        }
        catch (Exception e){
            fail();
        }

    }

    @Test
    public void testSetAbsentPlayerName(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Karlos";
        try{
            gm.setPlayerName(oldName, newName);
            fail();
        }
        catch (PlayerNameNotFoundException ignored){
            // test passed
        }
    }

    @Test
    public void testStartMatchWithPlayers(){
        GameMatch gm = new GameMatch();
        Player player = new Player("Dario");
        try{
            gm.addPlayer(player);
            gm.startMatch();
            assertEquals(IGameMatch.State.PLAY, gm.getState());
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testStartMatchWithoutPlayers(){
        GameMatch gm = new GameMatch();
        try{
            gm.startMatch();
            fail();
        }
        catch (NotEnoughPlayersException ignored){
            assertEquals(IGameMatch.State.SETUP, gm.getState());
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testPositionTile(){

    }

    @Test
    public void testPickNextTile(){

    }

    @Test
    public void testPickNextTileFail(){

    }

    @Test
    public void testEndMatch(){

    }

    @Test
    public void testEndMatchFail(){

    }

    @Test
    public void test1PMatch(){

    }


    @Test
    public void test2PMatch(){

    }
}
