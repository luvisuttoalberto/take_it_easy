package unittests.board;
import takeiteasy.core.board.*;
import org.junit.jupiter.api.Test;
import takeiteasy.core.board.exceptions.BadHexCoordinatesException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HexCoordinatesTest {

    @Test
    public void badCoordinatesTest(){
        assertThrows(BadHexCoordinatesException.class, ()-> new HexCoordinates(1,2,3));
    }
}