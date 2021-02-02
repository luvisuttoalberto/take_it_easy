package takeiteasy.player;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

public interface IPlayer {
    State getState();
    void setName(String name);
    String getName();
    public IBoard getBoard();

    void resetBoard();
    void startMatch() throws OutOfProperStateException;

    Tile showTileFromBoardAtCoordinates(HexCoordinates coordinates) throws OutOfBoardCoordinatesException;
    void placeTile(Tile tile, HexCoordinates coordinates) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, OutOfProperStateException;

    void transitionFromWaitingPlayersToPlacing() throws OutOfProperStateException;

    void leaveTheMatch();

    void endMatch() throws OutOfProperStateException;
    Integer computeScore();

    public enum State {
        Placing,
        WaitOther,
        WaitMatch,
        Leave;
    }
}
