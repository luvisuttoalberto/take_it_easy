package takeiteasy.player;

import org.json.JSONObject;
import takeiteasy.JSONKeys;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

public class Player implements IPlayer{

    private String Name;
    private State playerState;
    private IBoard playerBoard;

    public Player(String name) {
        Name = name;
        this.playerState = State.WAIT_MATCH;
        this.playerBoard = new BoardVanilla();
    }

    @Override
    public State getState() {
        return playerState;
    }

    @Override
    public void setName(String name) {
        Name = name;
    }

    @Override
    public String getName() { return Name; }

    @Override
    public void reset() {
        playerBoard = new BoardVanilla();
        playerState = State.WAIT_MATCH;
    }

    @Override
    public void startMatch() throws InvalidPlayerStateException {
        if (playerState == State.WAIT_MATCH) {
            playerState = State.PLACING;
        }
        else {
            throw new InvalidPlayerStateException();
        }
    }

    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException {
        if (playerState == State.PLACING) {
            playerBoard.placeTile(tile, coordinates);
            playerState = State.WAIT_OTHER;
        }
        else {
            throw new InvalidPlayerStateException();
        }
    }

    @Override
    public void transitionFromWaitingPlayersToPlacing() throws InvalidPlayerStateException {
        if (playerState == State.WAIT_OTHER) {
            playerState = State.PLACING;
        }
        else {
            throw new InvalidPlayerStateException();
        }
    }

    @Override
    public void endMatch() throws InvalidPlayerStateException {
        if (playerState == State.WAIT_OTHER) {
            playerState = State.WAIT_MATCH;
        }
        else {
            throw new InvalidPlayerStateException();
        }
    }

    @Override
    public JSONObject getData(){
        JSONObject data = new JSONObject();
        data.put(JSONKeys.PLAYER_NAME, Name);
        data.put(JSONKeys.PLAYER_STATE, playerState.name());
        data.put(JSONKeys.PLAYER_BOARD, playerBoard.getData());
        data.put(JSONKeys.PLAYER_SCORE, playerBoard.computeScore());
        return data;
    }

//    //TODO: Remove?
//    private HexCoordinates getCoordinatesFromUser() throws BadHexCoordinatesException {
//        Scanner sc = new Scanner(System.in);
//        Integer index1 = sc.nextInt();
//        Integer index2 = sc.nextInt();
//        Integer index3 = sc.nextInt();
//        return new HexCoordinates(index1, index2, index3);
//    }
}
