package takeiteasy.board;
import org.json.JSONObject;
import takeiteasy.tilepool.Tile;

public interface IBoard {
    void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupidedException;
    Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException;
    Integer computeScore();
    JSONObject getData();
}