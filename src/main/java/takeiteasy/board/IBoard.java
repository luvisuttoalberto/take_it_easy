package takeiteasy.board;
import org.json.JSONObject;
import takeiteasy.board.exceptions.*;
import takeiteasy.tilepool.Tile;

public interface IBoard {
    void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupiedException;
    Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException;
    Integer computeScore();
    JSONObject getData();
}