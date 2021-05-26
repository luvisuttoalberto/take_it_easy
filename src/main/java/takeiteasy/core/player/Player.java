package takeiteasy.core.player;

import org.json.JSONObject;
import takeiteasy.core.JSONKeys;
import takeiteasy.core.board.*;
import takeiteasy.core.player.exceptions.InvalidPlayerStateException;
import takeiteasy.core.board.exceptions.*;
import takeiteasy.core.tilepool.Tile;

public class Player implements IPlayer{

    String name;
    State playerState;
    IBoard playerBoard;

    public Player(String name) {
        this.name = name;
        this.playerState = State.WAIT_MATCH;
        this.playerBoard = new BoardVanilla();
    }

    @Override
    public State getState() {
        return playerState;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() { return name; }

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
    public void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupiedException, InvalidPlayerStateException {
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
        data.put(JSONKeys.PLAYER_NAME, name);
        data.put(JSONKeys.PLAYER_STATE, playerState.name());
        data.put(JSONKeys.PLAYER_BOARD, playerBoard.getData());
        data.put(JSONKeys.PLAYER_SCORE, playerBoard.computeScore());
        return data;
    }

}
