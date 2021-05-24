package takeiteasy.player;
import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.board.exceptions.BadHexCoordinatesException;
import takeiteasy.board.exceptions.CoordinatesOccupidedException;
import takeiteasy.board.exceptions.OutOfBoardCoordinatesException;
import takeiteasy.player.exceptions.InvalidPlayerStateException;
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
