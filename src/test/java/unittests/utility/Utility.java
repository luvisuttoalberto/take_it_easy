package unittests.utility;

import takeiteasy.board.*;
import takeiteasy.gamematch.GameMatch;
import takeiteasy.tilepool.Tile;
import takeiteasy.tilepool.TilePool;
import static takeiteasy.utility.Utility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

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

    static HexCoordinates[] generateCoordinateSequence(Integer score){
        HexCoordinates[] coords = generateCoordinateStandard();
        if(score == 27 || score == 54){
            Collections.swap(Arrays.asList(coords), 7,11);
        }
        if(score == 54){
            Collections.swap(Arrays.asList(coords), 13,17);
        }
        return coords;
    }


    public static ArrayList<Pair<Tile, HexCoordinates>> getTilesAndCoordinatesBoard11(Integer score){
        Tile[] tiles = generateTileListFromSeed(11);
        HexCoordinates[] coords = generateCoordinateSequence(score);

        ArrayList<Pair<Tile, HexCoordinates>> pairs = new ArrayList<>();
        IntStream.range(0, tiles.length)
                .forEach(iii -> pairs.add(new Pair<>(tiles[iii], coords[iii])));
        
        return pairs;
    }

    public static void SimulateCompleteGameMatch(GameMatch gm, String name, long tilePoolSeed) {
        ArrayList<Pair<Tile, HexCoordinates>> tilesAndCoords = getTilesAndCoordinatesBoard11(54);
        try {
            gm.addPlayer(name);
            gm.setTilePoolSeed(tilePoolSeed);
            gm.startMatch();

            IntStream.range(0, tilesAndCoords.size()-1)
                    .mapToObj(iii -> tilesAndCoords.get(iii).coordinate)
                    .forEach(coordinate -> {
                        try {
                            gm.positionCurrentTileOnPlayerBoard(name, coordinate);
                            gm.dealNextTile();
                        }
                        catch (Exception ignored) {}
                    });
            gm.positionCurrentTileOnPlayerBoard(name, tilesAndCoords.get(tilesAndCoords.size() - 1).coordinate);
            gm.endMatch();
        }
        catch(Exception e){
            fail();
        }
    }
}
