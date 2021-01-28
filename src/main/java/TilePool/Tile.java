package TilePool;

public class Tile {

    Integer top;
    Integer left;
    Integer right;

    public Integer getTop() {
        return top;
    }

    public Integer getLeft() {
        return left;
    }

    public Integer getRight() {
        return right;
    }

    public Tile(Integer top, Integer left, Integer right) {
        // TODO: implement validity check
        this.top = top;
        this.left = left;
        this.right = right;
    }
}
