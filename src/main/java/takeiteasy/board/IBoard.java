package takeiteasy.board;
import takeiteasy.tilepool.Tile;

public interface IBoard {
    void placeTile(Tile tile, HexCoordinates coordinates);
    Tile getTile(HexCoordinates coordinates);
    Integer computeScore();
}