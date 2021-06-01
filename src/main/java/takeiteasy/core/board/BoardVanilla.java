package takeiteasy.core.board;

import javafx.util.Pair;
import org.json.JSONObject;
import takeiteasy.core.board.exceptions.*;
import takeiteasy.core.tilepool.Tile;

import java.util.Arrays;
import java.util.stream.IntStream;

import static takeiteasy.utility.Utility.generateCoordinateStandard;

public class BoardVanilla implements IBoard {

    final Tile[][] tileStorage = new Tile[5][5];

    Boolean areCoordinatesInRange(HexCoordinates coordinates){
        return (-3<coordinates.getX() && coordinates.getX()<3) &&
               (-3<coordinates.getY() && coordinates.getY()<3) &&
               (-3<coordinates.getZ() && coordinates.getZ()<3);
    }

    Integer[] getStorageIndicesFromCoordinates(HexCoordinates coordinates){
        return new Integer[]{coordinates.getX()+2,coordinates.getY()+2};
    }

    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupiedException {
        if (!areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }

        Integer[] storageIdx = this.getStorageIndicesFromCoordinates(coordinates);
        if(tileStorage[storageIdx[0]][storageIdx[1]] != null){
            throw new CoordinatesOccupiedException();
        }

        tileStorage[storageIdx[0]][storageIdx[1]] = tile;

    }

    Tile getTile(HexCoordinates coordinates){
        Integer[] storageIdx = getStorageIndicesFromCoordinates(coordinates);
        return tileStorage[storageIdx[0]][storageIdx[1]];
    }

    enum RowOrientation{
        TOP,
        LEFT,
        RIGHT
    }

    Integer getTileNumberAtOrientation(Tile tile, RowOrientation orientation){
        switch (orientation) {
            case LEFT  : return tile.getLeft();
            case RIGHT : return tile.getRight();
            default    : return tile.getTop();
        }
    }

    Tile getTileAtCounterRotatedCoordinates(HexCoordinates coordinates, RowOrientation counterRotation) {
        switch (counterRotation) {
            case LEFT  : return getTile(coordinates.rotateRight());
            case RIGHT : return getTile(coordinates.rotateLeft());
            default    : return getTile(coordinates);
        }
    }

    Integer retrieveCellValueAtOrientation(Integer rowIndex, Integer lineIndexOfTile, RowOrientation rowOrientation){
        Integer cellValue = 0;
        try{
            HexCoordinates coordinates = new HexCoordinates(
                    rowIndex,
                    2 - Math.max(0,rowIndex) - lineIndexOfTile,
                    -2 - Math.min(rowIndex,0) + lineIndexOfTile
            );
            Tile tile = getTileAtCounterRotatedCoordinates(coordinates, rowOrientation);
            cellValue = getTileNumberAtOrientation(tile, rowOrientation);
        }
        catch (Exception ignored){
        }
        return cellValue;
    }

    Integer computeRowLength(Integer rowIndex){
        return 5-Math.abs(rowIndex);
    }

    Boolean isRowValid(Integer rowNumber, Integer rowIndex, RowOrientation rowOrientation){
        return IntStream.range(0, computeRowLength(rowIndex))
                .map(i -> retrieveCellValueAtOrientation(rowIndex, i, rowOrientation))
                .allMatch(cellValue -> cellValue == rowNumber);
    }

    Integer computeRowScore(Integer rowIndex, RowOrientation rowOrientation){
        // retrieve row number from first tile
        Integer rowNumber = retrieveCellValueAtOrientation(rowIndex, 0, rowOrientation);

        if(isRowValid(rowNumber, rowIndex, rowOrientation)){
            return rowNumber * computeRowLength(rowIndex);
        }
        else{
            return 0;
        }
    }

    @Override
    public Integer computeScore() {
        return Arrays.stream(RowOrientation.values())
                .flatMap(ro -> IntStream.rangeClosed(-2,2).mapToObj(iii-> new Pair<>(iii,ro)))
                .mapToInt(pair -> computeRowScore(pair.getKey(),pair.getValue()))
                .sum();
    }

    @Override
    public JSONObject getData() {
        JSONObject boardData = new JSONObject();

        Arrays.stream(generateCoordinateStandard())
                .map(hc -> new Pair<>(hc, getTile(hc)))
                .filter(hc_t -> hc_t.getValue()!=null)
                .forEach(hc_t -> boardData.put(hc_t.getKey().toString(), hc_t.getValue().getData()));

        return boardData;
    }
}
