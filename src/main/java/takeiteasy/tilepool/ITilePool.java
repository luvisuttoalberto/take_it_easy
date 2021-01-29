package takeiteasy.tilepool;

public interface ITilePool {

    Tile getTile(Integer index);

    void reset(long seed);

    long getSeed();

}
