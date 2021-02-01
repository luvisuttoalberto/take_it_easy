package takeiteasy.gamematch;

import takeiteasy.board.HexCoordinates;
import takeiteasy.tilepool.Tile;

public interface IGameMatch {

    enum State{
        SETUP,
        PLAY,
        PAUSE,
        FINISH;
    }

    void addPlayer();
    void setPlayerName(String name, Integer playerIndex);
    void removePlayer();

    void startMatch();

    Tile getCurrentTile();
    void positionCurrentTileOnPlayerBoard(Integer playerIndex, HexCoordinates coordinates);

    Boolean checkIfThereAreActivePlayers();
    Boolean checkIfPlayersAreWaitingForTile();
    void pickNextTile();


    void endMatch();

}
