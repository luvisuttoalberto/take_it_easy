package takeiteasy.gamematch;

import takeiteasy.board.*;
import takeiteasy.player.IPlayer;
import takeiteasy.player.InvalidPlayerStateException;
import takeiteasy.tilepool.Tile;

import java.util.Dictionary;

public interface IGameMatch {

    enum State{
        SETUP,
        PLAY,
        PAUSE,
        FINISH;

    }
    State getState();

    Integer getCurrentTileIndex();
    String[] getPlayerNames();
    Tile getCurrentTile();
    IBoard getBoardFromPlayerName(String playerName) throws PlayerNameNotFoundException;

    void addPlayer(String playerName) throws PlayerWithSameNameCannotBeAddedException, InvalidMatchStateException;

    void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException, InvalidMatchStateException;
    void removePlayer(String playerName) throws PlayerNameNotFoundException;
    void setTilePoolSeed(long seed) throws InvalidMatchStateException;
    void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException, InvalidPlayerStateException;

    void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException;

    void dealNextTile() throws InvalidMatchStateException, NotEnoughPlayersException, PlayersNotReadyForNextTileException, TilePoolDepletedException;

    void backToSetup() throws InvalidMatchStateException;

    void endMatch() throws InvalidMatchStateException, TilePoolNotDepletedException, PlayersNotReadyToEndMatchException;

    Dictionary<String,Integer> computeScore() throws InvalidMatchStateException;

}
