package takeiteasy.gamematch;

import takeiteasy.board.HexCoordinates;
import takeiteasy.player.IPlayer;
import takeiteasy.tilepool.Tile;

public interface IGameMatch {

    enum State{
        SETUP,
        PLAY,
        PAUSE,
        FINISH;
    }

    void addPlayer(IPlayer player) throws PlayerWithSameNameCannotBeAddedException;
    void setPlayerName(String oldName, String newName);
    void removePlayer(String playerName);
    void setTilePoolSeed(long seed);
    String[] getPlayerNames();

    void startMatch();

    Tile getCurrentTile();
    void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates);

    Boolean checkIfThereAreActivePlayers();
    Boolean checkIfAllPlayersAreWaitingForTile();
    void pickNextTile();

    void abortMatch();

    void endMatch();

}
