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
    public void placeTile(Tile tile, HexCoordinates coordinates) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        playerBoard.placeTile(tile, coordinates);
    }

    @Override
    public void leaveTheMatch() {
        playerState = State.Leave;
    }

    @Override
    public Tile showTileFromBoardAtCoordinates(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        return playerBoard.getTile(coordinates);
    }

    @Override
    public Integer computeScore() {
        return playerBoard.computeScore();
    }

    private HexCoordinates getCoordinatesFromUser() throws BadHexCoordinatesException {
        Scanner sc = new Scanner(System.in);
        Integer index1 = sc.nextInt();
        Integer index2 = sc.nextInt();
        Integer index3 = sc.nextInt();
        return new HexCoordinates(index1, index2, index3);
    }
}
