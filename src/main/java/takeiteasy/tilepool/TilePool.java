package takeiteasy.tilepool;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TilePool implements ITilePool {

    private final Tile[] extractedTiles;
    public long seed;
    private final static Integer size = 19;

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public long getSeed(){
        return seed;
    }

    @Override
    public Tile getTile(Integer index) throws ArrayIndexOutOfBoundsException{
        if(index > size || index < 0){
            throw new ArrayIndexOutOfBoundsException();
        }
        return extractedTiles[index];
    }

    @Override
    public void reset(long seed) {
        Tile[] tileset = createFullTileSet();

        //TODO:Collections.shuffle(Arrays.asList(tileset), new Random(this.seed));
        this.seed = seed;
        Random rand = new Random(seed);
        Collections.shuffle(Arrays.asList(tileset), rand);

        System.arraycopy(tileset, 0, extractedTiles, 0, size);
    }

    private static long generateSeed(){
        return new Random().nextLong();
    }

    public TilePool(long seed){
        this.extractedTiles = new Tile[size];
        this.seed = seed;
        reset(this.seed);
    }

    public TilePool() {
        this(generateSeed());
    }

    private Tile[] createFullTileSet() {
        Tile[] tileSet = new Tile[27];
        try {
            for (int i = 0; i < tileSet.length; ++i) {
                tileSet[i] = new Tile(Tile.topValues[i / 9],
                        Tile.leftValues[(i / 3) % 3],
                        Tile.rightValues[i % 3]);
            }
        }
        catch (IllegalArgumentException ignored){
        }
        return tileSet;
    }
}
