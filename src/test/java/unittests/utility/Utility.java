package unittests.utility;

import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;
import takeiteasy.tilepool.TilePool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
        int[][] coordinateSet = {
                {-2, 2, 0}, {-2, 1, 1}, {-2, 0, 2},
                {-1, 2, -1}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, -2, 2}, {0, 1, -1}, {0, 0, 0}, {0, -1, 1}, {0, 2, -2},
                {1, 1, -2}, {2, -1, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {1, 0, -1}, {2, -2, 0}
        };
        HexCoordinates[] coords = new HexCoordinates[19];
        try{
            for (int i=0;i<19;i++){
                coords[i] = new HexCoordinates(coordinateSet[i][0],coordinateSet[i][1],coordinateSet[i][2]);
            }
        } catch(Exception ignored){

        }
        return coords;
    }

    private static HexCoordinates[] generateCoordinateSequence27(){
        HexCoordinates[] coords = generateCoordinateSequence54();
        Collections.swap(Arrays.asList(coords), 13,17);
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
        ArrayList<Pair<Tile, HexCoordinates>> pairs = new ArrayList<Pair<Tile, HexCoordinates>>();
        for (int i = 0; i < 19; ++i) {
            Pair<Tile, HexCoordinates> pair = new Pair<Tile, HexCoordinates>(tiles[i], coords[i]);
            pairs.add(pair);
        }

        return pairs;
    }

    public static void main(String[] args) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        ArrayList<Pair<Tile, HexCoordinates>> pairs = getTilesAndCoordinatesBoard11(27);
        for(int i = 0; i < 19; ++i){
            System.out.println("tile:" + "("+pairs.get(i).tile.getTop()+" "+pairs.get(i).tile.getLeft()+" "+pairs.get(i).tile.getRight()+" )"+
                    "("+pairs.get(i).coordinate.getX()+" "+pairs.get(i).coordinate.getY()+" "+pairs.get(i).coordinate.getZ()+") ");
        }
    }
}
