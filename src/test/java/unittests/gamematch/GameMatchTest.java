package unittests.gamematch;

import org.junit.jupiter.api.Test;
import takeiteasy.gamematch.GameMatch;
import takeiteasy.player.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GameMatchTest {

    @Test
    public void testAddPlayer(){
        GameMatch gm = new GameMatch();
        String plyName = "Dario";
        Player ply = new Player(plyName);
        try {
            gm.addPlayer(ply);
        }
        catch (Exception e){
            fail("player add failed");
        }


        for (String pname : gm.getPlayerNames()){
            System.out.println(pname);
            if (pname==plyName){
                return;
            }
        }
        fail();
        //assertTrue(Arrays.stream(gm.getPlayerNames()).anyMatch(plyName::equals));

    }

    @Test
    public void testRemovePlayer(){

    }

    @Test
    public void testSetPlayerName(){

    }

    @Test
    public void testStartMatchWithPlayers(){

    }

    @Test
    public void testStartMatchWithoutPlayers(){

    }

    @Test
    public void testPositionTile(){

    }

    @Test
    public void testPickNextTile(){

    }

    @Test
    public void testPickNextTileFail(){

    }

    @Test
    public void testEndMatch(){

    }

    @Test
    public void testEndMatchFail(){

    }

    @Test
    public void test1PMatch(){

    }


    @Test
    public void test2PMatch(){

    }
}
