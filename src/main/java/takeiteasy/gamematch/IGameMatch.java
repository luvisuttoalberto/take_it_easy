package takeiteasy.gamematch;

import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.player.InvalidPlayerStateException;

public interface IGameMatch {

    enum State{
        SETUP,
        PLAY,
        PAUSE,
        FINISH
    }

    void addPlayer(String playerName) throws PlayersWithSameNameNotAllowedException, InvalidMatchStateException;

    void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException, InvalidMatchStateException, PlayersWithSameNameNotAllowedException;
    void removePlayer(String playerName) throws PlayerNameNotFoundException, NotEnoughPlayersException;
    void setTilePoolSeed(long seed) throws InvalidMatchStateException;
    void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException, InvalidPlayerStateException;

    void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException, InvalidPlayerStateException;

    void dealNextTile() throws InvalidMatchStateException, NotEnoughPlayersException, PlayersNotReadyForNextTileException, TilePoolDepletedException;

    void backToSetup() throws InvalidMatchStateException;

    void endMatch() throws InvalidMatchStateException, TilePoolNotDepletedException, PlayersNotReadyToEndMatchException, InvalidPlayerStateException;

    JSONObject getData();

}
