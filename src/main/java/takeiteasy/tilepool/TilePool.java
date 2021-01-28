package takeiteasy.tilepool;

public class TilePool implements ITilePool {
    @Override
    public Tile get_tile(Integer index) {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public void reset(long seed) {

    }

    private Tile[] createFullTileSet() {
        Tile[] tileSet = new Tile[27];
        for (int i=0; i<27; ++i) {
            tileSet[i] = new Tile(Tile.topValues[i/9],
                                  Tile.leftValues[(i/3)%3],
                                  Tile.rightValues[i%3]);
        }
        return tileSet;
    }
}
