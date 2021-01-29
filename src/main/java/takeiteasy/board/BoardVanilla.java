package takeiteasy.board;

import takeiteasy.tilepool.Tile;

public class BoardVanilla implements IBoard {

    private Boolean areCoordinatesInRange(HexCoordinates coordinates){
        return (-3<coordinates.getX() && coordinates.getX()<3) &&
               (-3<coordinates.getY() && coordinates.getY()<3) &&
               (-3<coordinates.getZ() && coordinates.getZ()<3);
    }

    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) {

    }

    @Override
    public Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        if (!this.areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }
        return null;
    }


    @Override
    public Integer computeScore() {
        return null;
    }
}
