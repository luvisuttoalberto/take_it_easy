package takeiteasy.core.gamematch;

import org.json.JSONObject;
import takeiteasy.core.board.*;
import takeiteasy.core.board.exceptions.*;
import takeiteasy.core.gamematch.exceptions.*;
import takeiteasy.core.player.exceptions.InvalidPlayerStateException;

public interface IGameMatch {

    enum State{
        SETUP,
        PLAY,
        FINISH
    }

    void addPlayer(String playerName) throws PlayersWithSameNameNotAllowedException, InvalidMatchStateException;

    void setPlayerName(String oldName, String newName) throws PlayerNameNotFoundException, InvalidMatchStateException, PlayersWithSameNameNotAllowedException;
    void removePlayer(String playerName) throws PlayerNameNotFoundException, NotEnoughPlayersException, LastPlacingPlayerRemovedException;
    void setTilePoolSeed(long seed) throws InvalidMatchStateException;
    void startMatch() throws InvalidMatchStateException, NotEnoughPlayersException;

    void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, OutOfBoardCoordinatesException, CoordinatesOccupiedException, InvalidPlayerStateException;

    void dealNextTile() throws InvalidMatchStateException, PlayersNotReadyForNextTileException, TilePoolDepletedException;

    void backToSetup() throws InvalidMatchStateException;

    void endMatch() throws InvalidMatchStateException, TilePoolNotDepletedException, PlayersNotReadyToEndMatchException;

    JSONObject getData();

}
