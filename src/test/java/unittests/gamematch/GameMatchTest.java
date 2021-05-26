package unittests.gamematch;

import org.json.JSONArray;
import org.json.JSONObject;
import takeiteasy.JSONKeys;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import takeiteasy.board.HexCoordinates;
import takeiteasy.gamematch.*;
import takeiteasy.gamematch.exceptions.*;
import takeiteasy.tilepool.*;
import unittests.utility.*;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static unittests.utility.Utility.SimulateCompleteGameMatch;
import static unittests.utility.Utility.getTilesAndCoordinatesBoard11;

public class GameMatchTest {

    @Test
    public void testConsistentGetData(){
        GameMatch gm = new GameMatch();
        JSONObject data = gm.getData();

        assertNotNull(data.opt(JSONKeys.MATCH_PLAYERS));
        assertNotNull(data.opt(JSONKeys.MATCH_CURRENT_TILE));
        assertNotNull(data.opt(JSONKeys.MATCH_SEED));
        assertNotNull(data.opt(JSONKeys.MATCH_STATE));
    }

    @Test
    public void testAddPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";

        assertDoesNotThrow(() -> gm.addPlayer(plyName));

        JSONObject data = gm.getData();
        assertEquals("SETUP", data.get(JSONKeys.MATCH_STATE));
        assertTrue(data.getJSONArray(JSONKeys.MATCH_PLAYERS).toString().contains("\"playerName\":\""+plyName+"\""));
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
    public void testSetTilePoolSeed(){
        GameMatch gm = new GameMatch();
        long tilePoolSeed = 11;

        assertDoesNotThrow(() -> gm.setTilePoolSeed(tilePoolSeed));
        assertEquals(tilePoolSeed, gm.getData().get(JSONKeys.MATCH_SEED));
    }

    @Test
    public void testStartMatch(){
        GameMatch gm = new GameMatch();
        String playerName = "Dario";
        try{
            gm.addPlayer(playerName);
        }
        catch (Exception ignored){
        }
        assertDoesNotThrow(gm::startMatch);
        assertEquals("PLAY", gm.getData().get(JSONKeys.MATCH_STATE));
    }

    @Test
    public void testPositionTile(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        try{
            gm.addPlayer(name);
            gm.startMatch();
            HexCoordinates coords = new HexCoordinates(0,0,0);

            assertDoesNotThrow( () -> gm.positionCurrentTileOnPlayerBoard(name, coords));

            JSONObject data = gm.getData();
            JSONObject playersData = data.getJSONObject(JSONKeys.MATCH_PLAYERS);
            JSONObject playerData = playersData.getJSONObject(name);
            JSONObject boardData = playerData.getJSONObject(JSONKeys.PLAYER_BOARD);
            JSONObject insertedTileData = boardData.getJSONObject(coords.toString());

            JSONObject currentTileData = data.getJSONObject(JSONKeys.MATCH_CURRENT_TILE);

            JSONAssert.assertEquals(currentTileData, insertedTileData, true);
        }
        catch (Exception ignored){
        }
    }

    @Test
    public void testDealNextTile(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        long tilePoolSeed = 11;
        try{
            gm.addPlayer(name);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();
            gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(0,0,0));
        }
        catch (Exception ignored){
        }

        assertDoesNotThrow(gm::dealNextTile);
    }

    @Test
    public void testEndMatch(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        long tilePoolSeed = 11;

        assertDoesNotThrow(() -> SimulateCompleteGameMatch(gm, name, tilePoolSeed));
        assertEquals("FINISH", gm.getData().get(JSONKeys.MATCH_STATE));
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

        JSONArray players = gm.getData().getJSONArray(JSONKeys.MATCH_PLAYERS);
        assertTrue(players.toString().contains("\"playerName\":\""+newName+"\""));
        assertFalse(players.toString().contains("\"playerName\":\""+oldName+"\""));
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

        JSONArray players = gm.getData().getJSONArray(JSONKeys.MATCH_PLAYERS);
        assertFalse(players.toString().contains("\"playerName\":\""+plyName+"\""));
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
    public void testRemoveLastPlacingPlayer(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Carlos";
        try {
            gm.addPlayer(name);
            gm.addPlayer(otherName);
            gm.startMatch();
            gm.positionCurrentTileOnPlayerBoard(name, new HexCoordinates(0,0,0));
        }
        catch (Exception ignored){
        }

        assertThrows(LastPlacingPlayerRemovedException.class, () -> gm.removePlayer(otherName));
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

        assertThrows(PlayerNameNotFoundException.class, () -> gm.setPlayerName(oldName, newName));
    }

    @Test
    public void testSetSeedDuringPlay(){
        GameMatch gm = new GameMatch();
        long seed = 11;
        try {
            gm.addPlayer("Dario");
            gm.startMatch();
        }
        catch(Exception ignored){
        }

        assertThrows(InvalidMatchStateException.class, () -> gm.setTilePoolSeed(seed));
    }

    @Test
    public void testSetSeedDuringFinish(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        long tilePoolSeed = 11;
        SimulateCompleteGameMatch(gm, name, tilePoolSeed);

        assertThrows(InvalidMatchStateException.class, () -> gm.setTilePoolSeed(tilePoolSeed));
    }

    @Test
    public void testStartMatchWithoutPlayers(){
        GameMatch gm = new GameMatch();

        assertThrows(NotEnoughPlayersException.class, gm::startMatch);
        assertEquals("SETUP", gm.getData().get(JSONKeys.MATCH_STATE));
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
            gm.positionCurrentTileOnPlayerBoard(otherName, new HexCoordinates(0,0,0));
        }
        catch (Exception ignored){
        }

        assertThrows(PlayersNotReadyForNextTileException.class, gm::dealNextTile);
    }

    @Test
    public void testDealNextTileTilePoolOver(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        long tilePoolSeed = 11;

        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);

        try {
            gm.addPlayer(name);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();
        }
        catch (Exception ignored){
        }

        IntStream.range(0, tilesAndCoords.size()-1)
                .mapToObj(iii -> tilesAndCoords.get(iii).coordinate)
                .forEach(coordinate -> {
                    try{
                        gm.positionCurrentTileOnPlayerBoard(name, coordinate);
                        gm.dealNextTile();
                    }
                    catch (Exception ignored) {}
                });

        try{
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(tilesAndCoords.size() - 1).coordinate);
        }
        catch (Exception ignored) {}
        assertThrows(TilePoolDepletedException.class, gm::dealNextTile);
    }

    @Test
    public void testBackToSetup(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        try{
            gm.addPlayer(name);
            gm.startMatch();
            HexCoordinates coords = new HexCoordinates(0,0,0);
            gm.positionCurrentTileOnPlayerBoard(name, coords);
            gm.dealNextTile();

        }
        catch (Exception ignored){
        }

        assertDoesNotThrow(gm::backToSetup);

        assertEquals("SETUP", gm.getData().get(JSONKeys.MATCH_STATE));
        JSONObject boardData = gm.getData().getJSONArray(JSONKeys.MATCH_PLAYERS).getJSONObject(0).getJSONObject(JSONKeys.PLAYER_BOARD);
        assertTrue(boardData.isEmpty());
    }

    @Test
    public void testBackToSetupDuringSetup(){
        GameMatch gm = new GameMatch();

        assertThrows(InvalidMatchStateException.class, gm::backToSetup);
    }

    @Test
    public void testEndMatchDuringSetup(){
        GameMatch gm = new GameMatch();

        assertThrows(InvalidMatchStateException.class, gm::endMatch);
    }

    @Test
    public void testEndMatchDuringPlayTilePoolNotFinished(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        try{
            gm.addPlayer(name);
            gm.startMatch();
        }
        catch (Exception ignored){
        }

        assertThrows(TilePoolNotDepletedException.class, gm::endMatch);
    }

    @Test
    public void testEndMatchDuringPlayPlayersNotReady(){
        GameMatch gm = new GameMatch();

        String name = "Dario";
        String otherName = "Carlos";
        Integer finalScore = 54;
        Integer otherFinalScore = 27;
        long tilePoolSeed = 11;
        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(finalScore);
        ArrayList<Pair<Tile, HexCoordinates>> otherTilesAndCoords = getTilesAndCoordinatesBoard11(otherFinalScore);

        try{
            gm.addPlayer(name);
            gm.addPlayer(otherName);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();
        }
        catch (Exception ignored){
        }

        IntStream.range(0, tilesAndCoords.size()-1)
                .forEach(iii -> {
                    try{
                        gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(iii).coordinate);
                        gm.positionCurrentTileOnPlayerBoard(otherName, otherTilesAndCoords.get(iii).coordinate);
                        gm.dealNextTile();
                    }
                    catch (Exception ignored){}
                });
        try{
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(tilesAndCoords.size()-1).coordinate);
        }
        catch (Exception ignored){
        }

        assertThrows(PlayersNotReadyToEndMatchException.class, gm::endMatch);
    }

    @Test
    public void testEndMatchDuringFinish(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        long tilePoolSeed = 11;
        SimulateCompleteGameMatch(gm, name, tilePoolSeed);

        assertThrows(InvalidMatchStateException.class, gm::endMatch);
    }

    @Test
    public void test2PMatch(){
        GameMatch gm = new GameMatch();
        String name = "Dario";
        String otherName = "Carlos";
        Integer finalScore = 54;
        Integer otherFinalScore = 27;
        long tilePoolSeed = 11;
        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(finalScore);
        ArrayList<Pair<Tile, HexCoordinates>> otherTilesAndCoords = getTilesAndCoordinatesBoard11(otherFinalScore);

        try {
            gm.addPlayer(name);
            gm.addPlayer(otherName);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();
        }
        catch (Exception e) {fail();}

        IntStream.range(0, tilesAndCoords.size()-1)
                .forEach(iii -> {
                    try{
                        gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(iii).coordinate);
                        gm.positionCurrentTileOnPlayerBoard(otherName, otherTilesAndCoords.get(iii).coordinate);
                        gm.dealNextTile();
                    }
                    catch (Exception e){fail();}
                });

        try{
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(tilesAndCoords.size()-1).coordinate);
            gm.positionCurrentTileOnPlayerBoard(otherName, tilesAndCoords.get(otherTilesAndCoords.size()-1).coordinate);
            gm.endMatch();
        }
        catch (Exception e) {fail();}

        JSONArray playersData = gm.getData().getJSONArray(JSONKeys.MATCH_PLAYERS);
        assertEquals(finalScore, playersData.getJSONObject(0).getInt(JSONKeys.PLAYER_SCORE));
        assertEquals(otherFinalScore, playersData.getJSONObject(1).getInt(JSONKeys.PLAYER_SCORE));
    }
}
