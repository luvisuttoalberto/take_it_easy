package unittests.utility;

import takeiteasy.board.*;
import takeiteasy.gamematch.GameMatch;
import takeiteasy.tilepool.Tile;
import takeiteasy.tilepool.TilePool;
import static takeiteasy.utility.Utility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;

public final class Utility {

    private static Tile[] generateTileListFromSeed(Integer seed){
        TilePool tilepool = new TilePool(seed);
        Tile[] tiles = new Tile[19];
        for (int i=0;i<19;++i){
            tiles[i] = tilepool.getTile(i);
        }
        return tiles;
    }

    private static HexCoordinates[] generateCoordinateSequence54(){
        HexCoordinates[] coords = generateCoordinateStandard();
        Collections.swap(Arrays.asList(coords), 7,11);
        Collections.swap(Arrays.asList(coords), 13,17);
        return coords;
    }

    private static HexCoordinates[] generateCoordinateSequence27(){
        HexCoordinates[] coords = generateCoordinateStandard();
        Collections.swap(Arrays.asList(coords), 7,11);
        return coords;
    }


    public static ArrayList<Pair<Tile, HexCoordinates>> getTilesAndCoordinatesBoard11(Integer score){
        Tile[] tiles = generateTileListFromSeed(11);
        HexCoordinates[] coords;
        if (score == 27){
            coords = generateCoordinateSequence27();
        }
        else {
            coords = generateCoordinateSequence54();
        }
        ArrayList<Pair<Tile, HexCoordinates>> pairs = new ArrayList<>();
        for (int i = 0; i < 19; ++i) {
            Pair<Tile, HexCoordinates> pair = new Pair<>(tiles[i], coords[i]);
            pairs.add(pair);
        }

        return pairs;
    }

    public static void SimulateCompleteGameMatch(GameMatch gm, String name, long tilePoolSeed) {
        try {
            ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);

            gm.addPlayer(name);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            for (int i = 0; i < tilesAndCoords.size() - 1; ++i) {
                gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(i).coordinate);
                gm.dealNextTile();
            }
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(18).coordinate);
            gm.endMatch();
        }
        catch(Exception ignored){
            fail();
        }
    }

    public static void main(String[] args){
        ArrayList<Pair<Tile, HexCoordinates>> pairs = getTilesAndCoordinatesBoard11(27);
        for(int i = 0; i < 19; ++i){
            System.out.println("tile:" + "("+pairs.get(i).tile.getTop()+" "+pairs.get(i).tile.getLeft()+" "+pairs.get(i).tile.getRight()+" )"+
                    "("+pairs.get(i).coordinate.getX()+" "+pairs.get(i).coordinate.getY()+" "+pairs.get(i).coordinate.getZ()+") ");
        }
    }
}
