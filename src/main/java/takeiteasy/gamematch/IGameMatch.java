package takeiteasy.gamematch;

import org.json.JSONObject;
import takeiteasy.board.*;
import takeiteasy.board.exceptions.BadHexCoordinatesException;
import takeiteasy.board.exceptions.CoordinatesOccupiedException;
import takeiteasy.board.exceptions.OutOfBoardCoordinatesException;
import takeiteasy.gamematch.exceptions.*;
import takeiteasy.player.exceptions.InvalidPlayerStateException;

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

    void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) throws PlayerNameNotFoundException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupiedException, InvalidPlayerStateException;

    void dealNextTile() throws InvalidMatchStateException, PlayersNotReadyForNextTileException, TilePoolDepletedException;

    void backToSetup() throws InvalidMatchStateException;

    void endMatch() throws InvalidMatchStateException, TilePoolNotDepletedException, PlayersNotReadyToEndMatchException;

    JSONObject getData();

}
