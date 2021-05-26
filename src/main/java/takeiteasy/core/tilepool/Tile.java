package takeiteasy.core.tilepool;

import org.json.JSONObject;
import takeiteasy.core.JSONKeys;

import java.util.Arrays;
import java.util.Objects;

public class Tile {

    public static final Integer[] topValues={1,5,9};
    public static final Integer[] leftValues={2,6,7};
    public static final Integer[] rightValues={3,4,8};

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

    public Tile(Integer top, Integer left, Integer right) throws IllegalArgumentException{
        if(!(Arrays.asList(topValues).contains(top)
                && Arrays.asList(leftValues).contains(left)
                && Arrays.asList(rightValues).contains(right)) ){
            throw new IllegalArgumentException();
        }
        this.top = top;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return top.equals(tile.top) && left.equals(tile.left) && right.equals(tile.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, left, right);
    }

    @Override
    public String toString() {
        return ""+left+","+top+","+right;
    }

    public JSONObject getData(){
        JSONObject tileData = new JSONObject();
        tileData.put(JSONKeys.TILE_TOP, top);
        tileData.put(JSONKeys.TILE_LEFT, left);
        tileData.put(JSONKeys.TILE_RIGHT, right);
        return tileData;
    }
}
