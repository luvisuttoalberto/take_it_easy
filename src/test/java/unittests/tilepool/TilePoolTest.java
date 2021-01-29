package unittests.tilepool;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import takeiteasy.tilepool.*;

public class TilePoolTest {

    @Test
    public void tileGetters() throws Exception{
        Tile t = new Tile(1,2,3);
        assertEquals(t.getTop(),1);
        assertEquals(t.getLeft(),2);
        assertEquals(t.getRight(),3);
    }

    @Test
    public void tileInBoundTest() throws Exception{
        TilePool pool = new TilePool();

    }

    @Test
    public void tileOutBoundTest() throws Exception{

    }

    @Test
    public void generationWithSeedTest() throws Exception{

        long seed = 1;
        TilePool tilePool1 = new TilePool(seed);
        TilePool tilePool2 = new TilePool(seed);

        Tile t1 = tilePool1.getTile(0);
        Tile t2 = tilePool2.getTile(0);

        assertEquals(t1.getTop(),t2.getTop());
        assertEquals(t1.getLeft(),t2.getLeft());
        assertEquals(t1.getRight(),t2.getRight());
    }

}
