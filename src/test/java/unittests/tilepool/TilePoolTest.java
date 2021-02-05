package unittests.tilepool;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import takeiteasy.tilepool.*;

import java.util.Random;

public class TilePoolTest {

    @Test
    public void testTileOutBound() throws Exception{
        try{
            TilePool pool = new TilePool(19);
            Tile t = pool.getTile(28);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ignored){
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testTileInBound() throws Exception{
        try{
            TilePool pool = new TilePool(19);
            for(int i = 0; i < pool.getSize(); ++i){
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
        Integer size = tilePool1.getSize();

        Random rand = new Random();
        int index = rand.nextInt(size);
        Tile t1 = tilePool1.getTile(index);
        Tile t2 = tilePool2.getTile(index);

        assertEquals(t1, t2);
    }

    @Test
    public void testAllDifferentTilesInATilePool() throws Exception{
        TilePool pool = new TilePool(1);
        for(int i = 0; i < pool.getSize() - 1; ++i){
            for(int j = i+1; j < pool.getSize(); ++j){
                Tile t1 = pool.getTile(i);
                Tile t2 = pool.getTile(j);

                assertNotEquals(t1, t2);
            }
        }
    }

    @Test
    public void testCorrectValueForTile() throws Exception{
        try{
            Tile t = new Tile(3,2,1);
            fail();
        }
        catch (IllegalArgumentException E){
        }
        catch (Exception e){
            fail();
        }
    }
}
