package unittests.gamematch;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import takeiteasy.board.HexCoordinates;
import takeiteasy.gamematch.*;
import takeiteasy.tilepool.*;
import unittests.utility.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unittests.utility.Utility.SimulateCompleteGameMatch;
import static unittests.utility.Utility.getTilesAndCoordinatesBoard11;

public class GameMatchTest {

    @Test
    public void testConsistentGetData(){
        GameMatch gm = new GameMatch();
        JSONObject data = gm.getData();
        assertNotNull(data.opt("players"));
        assertNotNull(data.opt("currentTile"));
        assertNotNull(data.opt("seed"));
        assertNotNull(data.opt("matchState"));
    }

    @Test
    public void testAddPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";

        assertDoesNotThrow(() -> gm.addPlayer(plyName));

        JSONObject data = gm.getData();
        assertEquals("SETUP", data.get("matchState"));
        assertTrue(data.getJSONObject("players").keySet().contains(plyName));
    }

    @Test
    public void testAddDuplicatePlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        try {
            gm.addPlayer(plyName);
        }
        catch (Exception ignored){
        }

        assertThrows(PlayersWithSameNameNotAllowedException.class, () -> gm.addPlayer(plyName));
    }

    @Test
    public void testAddPlayerDuringPlay(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Carlos";
        try{
            gm.addPlayer(name);
            gm.startMatch();
        }
        catch (Exception ignored){
        }
        assertThrows(InvalidMatchStateException.class, () -> gm.addPlayer(otherName));
    }

    @Test
    public void testAddPlayerDuringFinish(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Carlos";
        long tilePoolSeed = 11;

        SimulateCompleteGameMatch(gm, name, tilePoolSeed);
        assertThrows(InvalidMatchStateException.class, () -> gm.addPlayer(otherName));
    }

    @Test
    public void testSetPlayerNameDuringPlay(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Carlos";
        try{
            gm.addPlayer(oldName);
            gm.startMatch();
        }
        catch (Exception ignored){
        }
        assertThrows(InvalidMatchStateException.class, () -> gm.setPlayerName(oldName, newName));
    }

    @Test
    public void testSetPlayerNameDuringFinish(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Carlos";
        long tilePoolSeed = 11;

        SimulateCompleteGameMatch(gm, oldName, tilePoolSeed);
        assertThrows(InvalidMatchStateException.class, () -> gm.setPlayerName(oldName, newName));
    }

    @Test
    public void testRemovePlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        String otherPlyName = "Carlos";
        try {
            gm.addPlayer(plyName);
            gm.addPlayer(otherPlyName);
        }
        catch (Exception ignored){
        }
        assertDoesNotThrow( () -> gm.removePlayer(plyName));

        JSONObject data = gm.getData();
        assertFalse(data.getJSONObject("players").keySet().contains(plyName));
    }

    @Test
    public void testRemoveAbsentPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";

        assertThrows(PlayerNameNotFoundException.class, () -> gm.removePlayer(plyName));
    }

    @Test
    public void testRemoveLastPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        try {
            gm.addPlayer(plyName);
        }
        catch (Exception ignored){
        }

        assertThrows(NotEnoughPlayersException.class, () -> gm.removePlayer(plyName));
    }

    @Test
    public void testSetPlayerName(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Carlos";
        try{
            gm.addPlayer(oldName);
        }
        catch (Exception ignored){
        }

        assertDoesNotThrow( () -> gm.setPlayerName(oldName, newName));

        List<String> playerNames = new ArrayList<>(gm.getData().getJSONObject("players").keySet());
        assertTrue(playerNames.contains(newName));
        assertFalse(playerNames.contains(oldName));
    }

    @Test
    public void testSetAlreadyPresentPlayerName(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Carlos";
        try{
            gm.addPlayer(oldName);
            gm.addPlayer(newName);
        }
        catch (Exception ignored) {
        }
        assertThrows(PlayersWithSameNameNotAllowedException.class, () -> gm.setPlayerName(newName, oldName));
    }

    @Test
    public void testSetAbsentPlayerName(){
        GameMatch gm = new GameMatch();
        String oldName = "Dario";
        String newName = "Carlos";
        try{
            gm.setPlayerName(oldName, newName);
            fail();
        }
        catch (PlayerNameNotFoundException ignored){
            // test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testSetSeedDuringPlay(){
        GameMatch gm = new GameMatch();
        long seed = 11;
        try {
            gm.addPlayer("Dario");
            gm.startMatch();
            gm.setTilePoolSeed(seed);
            fail();
        }
        catch(InvalidMatchStateException ignored){
            //test passed
        }
        catch(Exception e){
            fail();
        }
    }

    @Test
    public void testSetSeedDuringFinish(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        long tilePoolSeed = 11;

        try{
            SimulateCompleteGameMatch(gm, name, tilePoolSeed);
            gm.setTilePoolSeed(tilePoolSeed);
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
    public void testSetTilePoolSeed(){
        GameMatch gm = new GameMatch();
        long tilePoolSeed = 11;
        try {
            gm.setTilePoolSeed(tilePoolSeed);
            assertEquals(tilePoolSeed, gm.getData().get("seed"));
        }
        catch(Exception ignored){
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
            assertEquals("SETUP", gm.getData().get("matchState"));
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testStartMatchWithPlayers(){
        GameMatch gm = new GameMatch();
        String playerName = "Dario";
        try{
            gm.addPlayer(playerName);
            gm.startMatch();
            assertEquals("PLAY", gm.getData().get("matchState"));
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
            gm.addPlayer(name);
            gm.startMatch();
            HexCoordinates coords = new HexCoordinates(0,0,0);
            gm.positionCurrentTileOnPlayerBoard(name, coords);

            JSONObject data = gm.getData();
            JSONObject playersData = data.getJSONObject("players");
            JSONObject playerData = playersData.getJSONObject(name);
            JSONObject boardData = playerData.getJSONObject("playerBoard");
            JSONObject insertedTileData = boardData.getJSONObject(coords.toString());

            JSONObject currentTileData = data.getJSONObject("currentTile");

            JSONAssert.assertEquals(currentTileData, insertedTileData, true);
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testDealNextTileNoPlayers(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Carlos";
        try{
            gm.addPlayer(name);
            gm.addPlayer(otherName);
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
        String otherName = "Carlos";
        try{
            gm.addPlayer(name);
            gm.addPlayer(otherName);
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
        long tilePoolSeed = 11;

        try{
            ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);

            gm.addPlayer(name);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            for (Pair<Tile, HexCoordinates> tilesAndCoord : tilesAndCoords) {
                gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoord.coordinate);
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
            gm.addPlayer(name);
            gm.startMatch();

            HexCoordinates coords = new HexCoordinates(0,0,0);
            gm.positionCurrentTileOnPlayerBoard(name, coords);

            gm.dealNextTile();

            gm.backToSetup();

            assertEquals("SETUP", gm.getData().get("matchState"));
            JSONObject boardData = gm.getData().getJSONObject("players").getJSONObject(name).getJSONObject("playerBoard");
            assertTrue(boardData.isEmpty());
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
            gm.addPlayer(name);
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
        String otherName = "Carlos";
        Integer finalScore = 54;
        Integer otherFinalScore = 27;
        long tilePoolSeed = 11;
        try{
            ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(finalScore);
            ArrayList<Pair<Tile, HexCoordinates>> otherTilesAndCoords = getTilesAndCoordinatesBoard11(otherFinalScore);

            gm.addPlayer(name);
            gm.addPlayer(otherName);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            for (int i=0; i<tilesAndCoords.size()-1; ++i) {
                gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(i).coordinate);
                gm.positionCurrentTileOnPlayerBoard(otherName, otherTilesAndCoords.get(i).coordinate);
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(18).coordinate);
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
        long tilePoolSeed = 11;

        try{
            SimulateCompleteGameMatch(gm, name, tilePoolSeed);
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
        long tilePoolSeed = 11;
        try{
            SimulateCompleteGameMatch(gm, name, tilePoolSeed);
            assertEquals("FINISH", gm.getData().get("matchState"));
        }
        catch (Exception e){
            fail();
        }

    }

    //TODO: is this test useful???
    @Test
    public void test2PMatch(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Carlos";
        Integer finalScore = 54;
        Integer otherFinalScore = 27;
        long tilePoolSeed = 11;
        try{
            ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(finalScore);
            ArrayList<Pair<Tile, HexCoordinates>> otherTilesAndCoords = getTilesAndCoordinatesBoard11(otherFinalScore);

            gm.addPlayer(name);
            gm.addPlayer(otherName);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            for (int i=0; i<tilesAndCoords.size()-1; ++i) {
                gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(i).coordinate);
                gm.positionCurrentTileOnPlayerBoard(otherName, otherTilesAndCoords.get(i).coordinate);
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(18).coordinate);
            gm.positionCurrentTileOnPlayerBoard(otherName, otherTilesAndCoords.get(18).coordinate);

            gm.endMatch();
            JSONObject playersData = gm.getData().getJSONObject("players");
            assertEquals(finalScore, playersData.getJSONObject(name).getInt("playerScore"));
            assertEquals(otherFinalScore, playersData.getJSONObject(otherName).getInt("playerScore"));
        }
        catch (Exception e){
            fail();
        }
    }
}
