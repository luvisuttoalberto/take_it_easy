package takeiteasy.player;
import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

public interface IPlayer {
    State getState();
    void setName(String name);
    String getName();

    void reset();
    void startMatch() throws InvalidPlayerStateException;

    void placeTile(Tile tile, HexCoordinates coordinates) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException;

    void transitionFromWaitingPlayersToPlacing() throws InvalidPlayerStateException;

    void endMatch() throws InvalidPlayerStateException;

    JSONObject getData();

    enum State {
        PLACING,
        WAIT_OTHER,
        WAIT_MATCH
    }
}
