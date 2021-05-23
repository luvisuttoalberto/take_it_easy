package takeiteasy.tilepool;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

public class TilePool implements ITilePool {

    final Tile[] extractedTiles;
    long seed;
    final static Integer size = 19;

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
    public void reset(long newSeed) {
        Tile[] tileset = createFullTileSet();
        this.seed=newSeed;
        Collections.shuffle(Arrays.asList(tileset),  new Random(this.seed));
        System.arraycopy(tileset, 0, extractedTiles, 0, size);
    }

    public TilePool(long seed){
        this.extractedTiles = new Tile[size];
        this.seed = seed;
        reset(this.seed);
    }

    public TilePool() {
        this(new Random().nextLong());
    }

    private Tile[] createFullTileSet() {
        Tile[] tileSet = new Tile[27];
        try {
            tileSet = IntStream.range(0,27).mapToObj(i->new Tile(Tile.topValues[i / 9],
                    Tile.leftValues[(i / 3) % 3],
                    Tile.rightValues[i % 3])).toArray(Tile[]::new);
        }
        catch (IllegalArgumentException ignored){
        }

        return tileSet;
    }
}
