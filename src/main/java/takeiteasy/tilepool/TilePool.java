package takeiteasy.tilepool;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TilePool implements ITilePool {

    private Tile[] extractedTiles;
    public long seed;

    @Override
    public long getSeed(){
        return this.seed;
    }

    @Override
    public Tile getTile(Integer index) throws Exception{
        if(index > 19 || index < 0){
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.extractedTiles[index];
    }

    @Override
    public void reset(long seed) {
        Random rand = new Random(seed);
        Tile[] tileset = createFullTileSet();
        Collections.shuffle(Arrays.asList(tileset), rand);
        for(int i = 0; i < this.extractedTiles.length; ++i){
            this.extractedTiles[i] = tileset[i];
        }
    }

    private static long generateSeed(){
        Random rand = new Random();
        return rand.nextLong();
    }

    public TilePool(long seed){
        this.extractedTiles = new Tile[19];
        this.seed = seed;
        reset(this.seed);
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
