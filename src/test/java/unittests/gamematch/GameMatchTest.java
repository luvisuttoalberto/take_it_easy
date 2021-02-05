package unittests.gamematch;

import org.junit.jupiter.api.Test;
import takeiteasy.board.HexCoordinates;
import takeiteasy.gamematch.*;
import takeiteasy.player.Player;
import takeiteasy.tilepool.Tile;
import takeiteasy.tilepool.TilePool;
import unittests.utility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

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
    public void testDealNextTileNoPlayers(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Karlos";
        try{
            gm.addPlayer(new Player(name));
            gm.addPlayer(new Player(otherName));
            gm.startMatch();
            gm.removePlayer(name);
            gm.removePlayer(otherName);
            gm.dealNextTile();
            fail();
        }
        catch (NotEnoughPlayersException ignored){
            // test pass
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testDealNextTilePlayersNotReady(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Karlos";
        try{
            gm.addPlayer(new Player(name));
            gm.addPlayer(new Player(otherName));
            gm.startMatch();
            gm.positionCurrentTileOnPlayerBoard(otherName,new HexCoordinates(0,0,0));
            gm.dealNextTile();
            fail();
        }
        catch (PlayersNotReadyForNextTileException ignored){
            // test pass
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testDealNextTilePlayersTilePoolOver(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        int[][] coordinateSet = {
                {-1, 2, -1}, {1, 1, -2}, {0, 0, 0},
                {-2, 2, 0}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {-2, 0, 2}, {0, -1, 1}, {0,-2, 2},
                {-2, 1, 1}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}, {2, -2, 0}
        };
        try{
            gm.addPlayer(new Player(name));
            gm.startMatch();

            for (int[] c : coordinateSet) {
                gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(c[0], c[1], c[2]));
                gm.dealNextTile();
            }
            fail();
        }
        catch (TilePoolDepletedException ignored){
            // test pass
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testAbortMatch(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        try{
            gm.addPlayer(new Player(name));
            gm.startMatch();

            HexCoordinates coords = new HexCoordinates(0,0,0);
            gm.positionCurrentTileOnPlayerBoard(name, coords);

            gm.dealNextTile();

            gm.backToSetup();

            assertEquals(IGameMatch.State.SETUP, gm.getState());
            assertEquals(0, gm.getCurrentTileIndex());
            assertEquals(null, gm.getBoardFromPlayerName(name).getTile(coords));
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testAbortMatchDuringSetup(){
        GameMatch gm = new GameMatch();
        try{
            gm.backToSetup();
            fail();
        }catch (InvalidMatchStateException ignored){
            // test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testEndMatchDuringSetup(){
        GameMatch gm = new GameMatch();
        try{
            gm.endMatch();
            fail();
        }catch (InvalidMatchStateException ignored){
            // test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testEndMatchDuringPlayTilePoolNotFinished(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        try{
            gm.addPlayer(new Player(name));
            gm.startMatch();

            gm.endMatch();
            fail();
        }catch (TilePoolNotDepletedException ignored){
            // test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testEndMatchDuringPlayPlayersNotReady(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Karlos";
        int[][] coordinateSet = {
                {-1, 2, -1}, {1, 1, -2}, {0, 0, 0},
                {-2, 2, 0}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {-2, 0, 2}, {0, -1, 1}, {0,-2, 2},
                {-2, 1, 1}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}
        };
        try{
            gm.addPlayer(new Player(name));
            gm.addPlayer(new Player(otherName));
            gm.startMatch();

            for (int[] c : coordinateSet) {
                gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(c[0], c[1], c[2]));
                gm.positionCurrentTileOnPlayerBoard(otherName, new HexCoordinates(c[0], c[1], c[2]));
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(2, -2, 0));
            gm.endMatch();
            fail();
        }
        catch (PlayersNotReadyToEndMatchException ignored){
            // test pass
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testEndMatchDuringFinish(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        Integer tilePoolSeed = 11; //TODO: seed

        try{
            //TODO: initialize inside
            ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = new ArrayList<Pair<Tile, HexCoordinates>>();
            Utility.PlaceTileInput(tilesAndCoords);

            gm.addPlayer(new Player(name));
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            for (Integer i=0;i<tilesAndCoords.size()-1;++i) {
                gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(i).coordinate);
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(18).coordinate);
            gm.endMatch();
            gm.endMatch();
            fail();
        }
        catch (InvalidMatchStateException ignored){
            // test pass
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testEndMatch(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        int[][] coordinateSet = {
                {-1, 2, -1}, {1, 1, -2}, {0, 0, 0},
                {-2, 2, 0}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {-2, 0, 2}, {0, -1, 1}, {0,-2, 2},
                {-2, 1, 1}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}
        };
        try{
            gm.addPlayer(new Player(name));
            gm.startMatch();

            for (int[] c : coordinateSet) {
                gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(c[0], c[1], c[2]));
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(2, -2, 0));
            gm.endMatch();
            assertEquals(IGameMatch.State.FINISH, gm.getState());
        }
        catch (Exception e){
            fail();
        }

    }

    @Test
    public void testComputeScoreDuringSetup(){
        GameMatch gm = new GameMatch();
        try{
            gm.computeScore();
            fail();
        }
        catch (InvalidMatchStateException ignored){
            // test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testComputeScoreDuringPlay(){
        GameMatch gm = new GameMatch();
        String name = "Dario";

        try{
            gm.addPlayer(new Player(name));
            gm.startMatch();
            gm.computeScore();
            fail();
        }
        catch (InvalidMatchStateException ignored){
            // test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testComputeScore(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        Integer tilePoolSeed = 11;
        Integer finalScore = 54;
        try{
            ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = new ArrayList<Pair<Tile, HexCoordinates>>();
            Utility.PlaceTileInput(tilesAndCoords);

            gm.addPlayer(new Player(name));
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            for (Integer i=0; i<tilesAndCoords.size()-1;++i) {
                gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(i).coordinate);
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(18).coordinate);

            gm.endMatch();
            Dictionary<String,Integer> playerScores = gm.computeScore();
            assertEquals(finalScore,playerScores.get(name));
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void test2PMatch(){

    }
}
