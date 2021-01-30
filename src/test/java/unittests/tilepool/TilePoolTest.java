package unittests.tilepool;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import takeiteasy.tilepool.*;

public class TilePoolTest {

    @Test
    public void testTileGetters() throws Exception{
        Tile t = new Tile(1,2,3);
        assertEquals(t.getTop(),1);
        assertEquals(t.getLeft(),2);
        assertEquals(t.getRight(),3);
    }

    @Test
    public void testTileOutBound() throws Exception{
        try{
            TilePool pool = new TilePool();
            Tile t = pool.getTile(28);
        }
        catch (ArrayIndexOutOfBoundsException E){

        }
    }

    @Test
    public void testTileInBound() throws Exception{
        try{
            TilePool pool = new TilePool();
            for(int i = 0; i < 19; ++i){
                Tile t = pool.getTile(i);
            }
        }
        catch (Exception E){
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testGenerationWithSeed() throws Exception{

        long seed = 1;
        TilePool tilePool1 = new TilePool(seed);
        TilePool tilePool2 = new TilePool(seed);

        Tile t1 = tilePool1.getTile(0);
        Tile t2 = tilePool2.getTile(0);

        assertEquals(t1.getTop(),t2.getTop());
        assertEquals(t1.getLeft(),t2.getLeft());
        assertEquals(t1.getRight(),t2.getRight());
    }

    @Test
    public void testAllDifferentTilesInATilePool() throws Exception{
        TilePool pool = new TilePool(1);
        for(int i = 0; i < 18; ++i){
            for(int j = i+1; j < 19; ++j){
                Tile t1 = pool.getTile(i);
                Tile t2 = pool.getTile(j);

                assertFalse(t1.getTop().equals(t2.getTop())
                                    && t1.getLeft().equals(t2.getLeft())
                                    && t1.getRight().equals(t2.getRight()));
            }
        }
    }



}
