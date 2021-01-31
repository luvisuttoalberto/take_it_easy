package takeiteasy.tilepool;

public interface ITilePool {

    Tile getTile(Integer index) throws Exception;

    void reset(long seed);

    long getSeed();

    Integer getSize();

}
