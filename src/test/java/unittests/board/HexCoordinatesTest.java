package unittests.board;
import takeiteasy.board.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class HexCoordinatesTest {

    @Test
    public void badCoordinatesTest(){
        try {
            HexCoordinates hc = new HexCoordinates(1, 2, 3);
            fail();
        }
        catch (BadHexCoordinatesException ignored){
            //test passed
        }
    }
}