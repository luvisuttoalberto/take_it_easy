package takeiteasy.board;

public class HexCoordinates {
    private Integer x;
    private Integer y;
    private Integer z;

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
        Integer buffer = this.x;
        this.x = this.y;
        this.y = this.z;
        this.z = buffer;

        return this;
    }

    public HexCoordinates rotateRight() {
        Integer buffer = this.x;
        this.x = this.z;
        this.z = this.y;
        this.y = buffer;

        return this;
    }
}
