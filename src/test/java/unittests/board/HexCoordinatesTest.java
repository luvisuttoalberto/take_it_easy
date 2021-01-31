package unittests.board;
import takeiteasy.board.*;
import org.junit.jupiter.api.Test;

public class HexCoordinatesTest {

    @Test
    public void badCoordinatesTest() throws Exception {
        try {
            HexCoordinates hc = new HexCoordinates(1, 2, 3);
        }
        catch (BadHexCoordinatesException ignored){
        }
    }
}