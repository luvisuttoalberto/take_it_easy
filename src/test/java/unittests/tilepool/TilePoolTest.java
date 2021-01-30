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
            TilePool pool = new TilePool(19);
            Tile t = pool.getTile(28);
        }
        catch (ArrayIndexOutOfBoundsException E){

        }
    }

    @Test
    public void testTileInBound() throws Exception{
        try{
            TilePool pool = new TilePool(19);
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
        int size = 19;
        TilePool tilePool1 = new TilePool(size, seed);
        TilePool tilePool2 = new TilePool(size, seed);
        
        // TODO: pick a random index
        Tile t1 = tilePool1.getTile(0);
        Tile t2 = tilePool2.getTile(0);

        assertTrue(t1.getTop().equals(t2.getTop())
                            && t1.getLeft().equals(t2.getLeft())
                            && t1.getRight().equals(t2.getRight()));
    }

    @Test
    public void testAllDifferentTilesInATilePool() throws Exception{
        int size = 19;
        long seed = 1;
        TilePool pool = new TilePool(size, seed);
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
