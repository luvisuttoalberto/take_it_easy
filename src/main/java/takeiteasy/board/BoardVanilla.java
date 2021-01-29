package takeiteasy.board;

import takeiteasy.tilepool.Tile;

public class BoardVanilla implements IBoard {
    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) {

    }

    @Override
    public Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        throw new OutOfBoardCoordinatesException();
        //return null;
    }

    @Override
    public Integer computeScore() {
        return null;
    }
}
