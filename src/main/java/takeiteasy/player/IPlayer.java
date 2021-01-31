package takeiteasy.player;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

public interface IPlayer {
    State getState();
    void setName(String name);
    String getName();
    void placeTile(Tile tile) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException;
    void leaveTheMatch();
    Tile showTileFromBoardAtCoordinates(HexCoordinates coordinates);
    Integer computeScore();
    public enum State {
        Placing,
        WaitOther,
        WaitMatch,
        Leave;
    }
}
