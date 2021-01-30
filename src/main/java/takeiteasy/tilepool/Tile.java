package takeiteasy.tilepool;

import java.util.Arrays;

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
        if(!(Arrays.stream(topValues).anyMatch(top::equals)
                && Arrays.stream(leftValues).anyMatch(left::equals)
                && Arrays.stream(rightValues).anyMatch(right::equals)) ){
            throw new IllegalArgumentException();
        }
        this.top = top;
        this.left = left;
        this.right = right;
    }
}
