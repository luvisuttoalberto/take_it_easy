package takeiteasy.core.board;

import javafx.util.Pair;
import org.json.JSONObject;
import takeiteasy.core.board.exceptions.*;
import takeiteasy.core.tilepool.Tile;

import java.util.Arrays;
import java.util.Objects;
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
        if(this.tileStorage[storageIdx[0]][storageIdx[1]] != null){
            throw new CoordinatesOccupiedException();
        }

        this.tileStorage[storageIdx[0]][storageIdx[1]] = tile;

    }

    @Override
    public Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        if (!this.areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }
        Integer[] storageIdx = this.getStorageIndicesFromCoordinates(coordinates);
        return this.tileStorage[storageIdx[0]][storageIdx[1]];
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

    Tile getTileAtCounterRotatedCoordinates(HexCoordinates coordinates, RowOrientation counterRotation) throws OutOfBoardCoordinatesException {
        switch (counterRotation) {
            case LEFT  : return getTile(coordinates.rotateRight());
            case RIGHT : return getTile(coordinates.rotateLeft());
            default    : return getTile(coordinates);
        }
    }

    Integer computeRowScore(Integer rowIndex,RowOrientation rowOrientation){

        // Get coordinates of first tile in the row
        Integer x0 = rowIndex,
                y0 = 2 - Math.max(0,x0),
                z0 = -2 - Math.min(x0,0) ;
        Integer rowLength = 5-Math.abs(rowIndex);

        Integer score = 0;
        try {

            // retrieve row number from first tile
            Tile tile = this.getTileAtCounterRotatedCoordinates(new HexCoordinates(x0, y0, z0), rowOrientation);
            Integer rowNumber = this.getTileNumberAtOrientation(tile, rowOrientation);

            // Scan tiles in row and check they all have the same number at given orientation
            for (int i = 0; i < rowLength; ++i) {

                Integer y = y0 - i;
                Integer z = z0 + i;
                tile = this.getTileAtCounterRotatedCoordinates(new HexCoordinates(x0, y, z), rowOrientation);
                Integer cellValue = this.getTileNumberAtOrientation(tile,rowOrientation);

                if (!cellValue.equals(rowNumber)) {
                    return 0;
                }
            }
            // compute score
            score = rowNumber * rowLength;
        }
        catch(Exception ignored){
        }

        return score;
    }
    //--------

    @Override
    public Integer computeScore() {
        return Arrays.stream(RowOrientation.values()).flatMap(ro ->
                IntStream.rangeClosed(-2,2).mapToObj(iii-> new Pair<>(iii,ro)))
                .mapToInt(pair->computeRowScore(pair.getKey(),pair.getValue())).sum();
    }

    @Override
    public JSONObject getData() {
        JSONObject boardData = new JSONObject();

        Arrays.stream(generateCoordinateStandard()).map(hc -> {
                try { return new Pair<>(hc, getTile(hc)); }
                catch (OutOfBoardCoordinatesException ignored) {}
                return null;
            })
            .filter(Objects::nonNull)
            .filter(hc_t->hc_t.getValue()!=null)
            .forEach(hc_t->boardData.put(
                    hc_t.getKey().toString(),
                    hc_t.getValue().getData())
             );

        return boardData;
    }
}
