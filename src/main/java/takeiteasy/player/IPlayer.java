package takeiteasy.player;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

public interface IPlayer {
    State getState();
    void setName(String name);
    String getName();

    void resetBoard();
    void startMatch();

    Tile showTileFromBoardAtCoordinates(HexCoordinates coordinates) throws OutOfBoardCoordinatesException;
    void placeTile(Tile tile, HexCoordinates coordinates) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException;

    void transitionFromWaitingPlayersToPlacing();

    void leaveTheMatch();

    void endMatch();
    Integer computeScore();

    public enum State {
        Placing,
        WaitOther,
        WaitMatch,
        Leave;
    }
}
