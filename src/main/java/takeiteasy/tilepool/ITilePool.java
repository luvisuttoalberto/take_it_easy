package takeiteasy.tilepool;

public interface ITilePool {

    Tile get_tile(Integer index);

    void reset();
    void reset(long seed);

}
