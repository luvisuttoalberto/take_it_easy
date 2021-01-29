package takeiteasy.tilepool;

import java.util.Random;

public class TilePool implements ITilePool {

    private Tile[] extractedTiles;
    public long seed;

    @Override
    public long getSeed(){
        return this.seed;
    }

    @Override
    public Tile getTile(Integer index) {
        // throws exception if out of index
        return this.extractedTiles[index];
    }

    @Override
    public void reset(long seed) {
        Random rand = new Random(seed);
        Tile[] tileSet = createFullTileSet();
        for(int i = 0; i < 19; ++i){
            int index = rand.nextInt(27-i);
            for(int j = index; j < tileSet.length - 1; ++j){
                tileSet[j] = tileSet[j + 1];
            }
            this.extractedTiles[i] = tileSet[index];        // should this be extractedTiles[i] ???
        }
    }

    private static long generateSeed(){
        Random rand = new Random();
        return rand.nextLong();
    }

    public TilePool(long seed){
        this.extractedTiles = new Tile[19];
        this.seed = seed;
        reset(this.seed); // this.seed or seed? Is it the same?
    }

    public TilePool() {
        this(generateSeed());
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
