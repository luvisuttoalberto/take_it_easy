package takeiteasy.board;

import takeiteasy.board.exceptions.BadHexCoordinatesException;

public class HexCoordinates {
    Integer x, y, z;

    public HexCoordinates(Integer x, Integer y, Integer z) throws BadHexCoordinatesException {
        if (x + y + z != 0) {
            throw new BadHexCoordinatesException();
        }
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getZ() {
        return z;
    }


    public HexCoordinates rotateLeft() {
        Integer buffer = x;
        x = y;
        y = z;
        z = buffer;

        return this;
    }

    public HexCoordinates rotateRight() {
        Integer buffer = x;
        x = z;
        z = y;
        y = buffer;

        return this;
    }

    @Override
    public String toString() {
        return ""+x+" "+y+" "+z;
    }
}
