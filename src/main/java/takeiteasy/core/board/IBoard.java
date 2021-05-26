package takeiteasy.core.board;
import org.json.JSONObject;
import takeiteasy.core.board.exceptions.*;
import takeiteasy.core.tilepool.Tile;

public interface IBoard {
    void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupiedException;
    Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException;
    Integer computeScore();
    JSONObject getData();
}