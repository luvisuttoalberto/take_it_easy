package takeiteasy.gamematch;

import takeiteasy.board.*;
import takeiteasy.player.IPlayer;
import takeiteasy.player.OutOfProperStateException;
import takeiteasy.tilepool.Tile;

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

    void addPlayer(IPlayer player) throws PlayerWithSameNameCannotBeAddedException;
    void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException;
    void removePlayer(String playerName) throws PlayerNameNotFoundException;
    void setTilePoolSeed(long seed);

    void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException, OutOfProperStateException;

    void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, OutOfProperStateException;

    void dealNextTile() throws InvalidMatchStateException, NotEnoughPlayersException, PlayerNotReadyForNextTile, TilePoolDepletedException;

    void abortMatch();

    void endMatch();

}
