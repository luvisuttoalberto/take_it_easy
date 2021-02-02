package unittests.gamematch;

import org.junit.jupiter.api.Test;
import takeiteasy.board.HexCoordinates;
import takeiteasy.gamematch.*;
import takeiteasy.player.Player;
import takeiteasy.tilepool.TilePool;

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
    public void testGetCurrentTile(){
        GameMatch gm = new GameMatch();
        long seed = 666;
        gm.setTilePoolSeed(seed);
        TilePool tilePool = new TilePool(seed);
        assertEquals(tilePool.getTile(0), gm.getCurrentTile());
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
    public void testPositionTile(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        try{
            gm.addPlayer(new Player(name));
            gm.startMatch();
            HexCoordinates coords = new HexCoordinates(0,0,0);
            gm.positionCurrentTileOnPlayerBoard(name, coords);
            assertEquals(gm.getCurrentTile(), gm.getBoardFromPlayerName(name).getTile(coords));
        }
        catch (Exception e){
            fail();
        }
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
