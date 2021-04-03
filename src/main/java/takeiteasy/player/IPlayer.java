package takeiteasy.player;
import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

public interface IPlayer {
    State getState();
    void setName(String name);
    String getName();
//    IBoard getBoard();

    void reset();
    void startMatch() throws InvalidPlayerStateException;

    Tile showTileFromBoardAtCoordinates(HexCoordinates coordinates) throws OutOfBoardCoordinatesException;
    void placeTile(Tile tile, HexCoordinates coordinates) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException;

    void transitionFromWaitingPlayersToPlacing() throws InvalidPlayerStateException;

    void endMatch() throws InvalidPlayerStateException;
//    Integer computeScore();

    JSONObject getData();

    enum State {
        PLACING,
        WAIT_OTHER,
        WAIT_MATCH
    }
}
