package takeiteasy.core.tilepool;

public interface ITilePool {

    Tile getTile(Integer index) throws ArrayIndexOutOfBoundsException;

    void reset(long seed);

    long getSeed();

    Integer getSize();

}
