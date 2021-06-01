package takeiteasy.core.player;
import org.json.JSONObject;
import takeiteasy.core.board.*;
import takeiteasy.core.board.exceptions.CoordinatesOccupiedException;
import takeiteasy.core.board.exceptions.OutOfBoardCoordinatesException;
import takeiteasy.core.player.exceptions.InvalidPlayerStateException;
import takeiteasy.core.tilepool.Tile;

public interface IPlayer {
    State getState();
    void setName(String name);
    String getName();

    void reset();
    void startMatch() throws InvalidPlayerStateException;

    void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupiedException, InvalidPlayerStateException;

    void transitionFromWaitingPlayersToPlacing() throws InvalidPlayerStateException;

    void endMatch() throws InvalidPlayerStateException;

    JSONObject getData();

    enum State {
        PLACING,
        WAIT_OTHER,
        WAIT_MATCH
    }
}
