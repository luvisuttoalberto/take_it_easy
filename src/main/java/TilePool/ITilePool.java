package TilePool;

public interface ITilePool {

    Tile get_tile(Integer index);

    void reset();
    void reset(long seed);

}
