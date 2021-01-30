package takeiteasy.board;

import takeiteasy.tilepool.Tile;

import java.util.Dictionary;
import java.util.Hashtable;

public class BoardVanilla implements IBoard {

    private Tile[][] tileStorage = new Tile[5][5];

    public BoardVanilla() {
    }

    private Boolean areCoordinatesInRange(HexCoordinates coordinates){
        return (-3<coordinates.getX() && coordinates.getX()<3) &&
               (-3<coordinates.getY() && coordinates.getY()<3) &&
               (-3<coordinates.getZ() && coordinates.getZ()<3);
    }

    private Integer[] getStorageIdxsFromCoordinates(HexCoordinates coordinates){
        return new Integer[]{coordinates.getX()+2,coordinates.getY()+2};

    }

    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        if (!areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }

        Integer[] storageIdx = this.getStorageIdxsFromCoordinates(coordinates);
        if(this.tileStorage[storageIdx[0]][storageIdx[1]] != null){
            throw new CoordinatesOccupidedException();
        }

        this.tileStorage[storageIdx[0]][storageIdx[1]] = tile;

    }

    @Override
    public Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        if (!this.areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }
        Integer[] storageIdx = this.getStorageIdxsFromCoordinates(coordinates);
        return this.tileStorage[storageIdx[0]][storageIdx[1]];
    }


    @Override
    public Integer computeScore() {
        return null;
    }
}
