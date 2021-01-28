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

}
