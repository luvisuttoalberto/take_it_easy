package unittests.tilepool;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import takeiteasy.core.tilepool.*;

import java.util.stream.IntStream;

public class TilePoolTest {

    @Test
    public void testTileOutBound(){
        TilePool pool = new TilePool(19);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> pool.getTile(28));
    }

    @Test
    public void testTileInBound(){
        TilePool pool = new TilePool(19);
        IntStream.range(0, pool.getSize())
                .forEach(assertDoesNotThrow(() -> pool::getTile));
    }

    @Test
    public void testGenerationWithSeed(){

        long seed = 1;
        TilePool tilePool1 = new TilePool(seed);
        TilePool tilePool2 = new TilePool(seed);
        Integer size = tilePool1.getSize();

        IntStream.range(0, size)
                .forEach(iii -> assertEquals(tilePool1.getTile(iii), tilePool2.getTile(iii)));
    }

    @Test
    public void testAllDifferentTilesInATilePool(){
        TilePool pool = new TilePool(1);
        IntStream.range(0, pool.getSize()-1)
                .forEach(iii ->
                        IntStream.range(iii + 1, pool.getSize())
                                .forEach(jjj -> assertNotEquals(pool.getTile(iii), pool.getTile(jjj)))
                );
    }

    @Test
    public void testCorrectValueForTile(){
        assertThrows(IllegalArgumentException.class, ()-> new Tile(3,2,1));
    }
}
