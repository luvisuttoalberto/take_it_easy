package unittests.tilepool;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import takeiteasy.tilepool.Tile;

public class TileTest {

    @Test
    public void testGetData(){
        Integer top = 9;
        Integer left = 2;
        Integer right = 3;
        Tile tile = new Tile(top,left,right);
        JSONObject tileData = tile.getData();
        assertEquals(top, tileData.get("top"));
        assertEquals(left, tileData.get("left"));
        assertEquals(right, tileData.get("right"));
    }
}
