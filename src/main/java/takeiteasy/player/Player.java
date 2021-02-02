package takeiteasy.player;

import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

import java.util.Scanner;

public class Player implements IPlayer{

    private String Name;
    private State playerState;
    BoardVanilla playerBoard;

    public Player(String name) {
        Name = name;
        this.playerState = State.WaitMatch;
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
    public String getName() {
        return Name;
    }

    @Override
    public void resetBoard() {
        playerBoard = new BoardVanilla();
    }

    @Override
    public void startMatch() throws OutOfProperStateException {
        if (playerState == State.WaitMatch) {
            playerState = State.Placing;
        }
        else {
            throw new OutOfProperStateException();
        }
    }


    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, OutOfProperStateException {
        if (playerState == State.Placing) {
            playerBoard.placeTile(tile, coordinates);
            playerState = State.WaitOther;
        }
        else {
            throw new OutOfProperStateException();
        }
    }

    @Override
    public void transitionFromWaitingPlayersToPlacing() throws OutOfProperStateException {
        if (playerState == State.WaitOther) {
            playerState = State.Placing;
        }
        else {
            throw new OutOfProperStateException();
        }
    }

    @Override
    public void leaveTheMatch() {
        playerState = State.Leave;
    }

    @Override
    public void endMatch() throws OutOfProperStateException {
        if (playerState == State.WaitOther) {
            playerState = State.WaitMatch;
        }
        else {
            throw new OutOfProperStateException();
        }
    }

    @Override
    public Integer computeScore() {
        return playerBoard.computeScore();
    }

    @Override
    public Tile showTileFromBoardAtCoordinates(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        return playerBoard.getTile(coordinates);
    }

    private HexCoordinates getCoordinatesFromUser() throws BadHexCoordinatesException {
        Scanner sc = new Scanner(System.in);
        Integer index1 = sc.nextInt();
        Integer index2 = sc.nextInt();
        Integer index3 = sc.nextInt();
        return new HexCoordinates(index1, index2, index3);
    }

}
