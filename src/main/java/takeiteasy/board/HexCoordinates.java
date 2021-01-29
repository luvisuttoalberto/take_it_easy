package takeiteasy.board;

public class HexCoordinates {
    private final Integer x;
    private final Integer y;
    private final Integer z;

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


}
