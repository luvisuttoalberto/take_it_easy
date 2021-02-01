package takeiteasy.gamematch;

import takeiteasy.board.HexCoordinates;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;

import java.util.Vector;

public class GameMatch implements IGameMatch{

    Vector<IPlayer> players = new Vector<IPlayer>();
    TilePool tilePool;

    public GameMatch() {
        tilePool = new TilePool();
    }

    @Override
    public void setTilePoolSeed(long seed){
        tilePool.reset(seed);
    }

    @Override
    public void addPlayer(IPlayer player) {

    }

    @Override
    public void setPlayerName(String name, Integer playerIndex) {

    }

    @Override
    public void removePlayer() {

    }

    @Override
    public void startMatch() {

    }

    @Override
    public Tile getCurrentTile() {
        return null;
    }

    @Override
    public void positionCurrentTileOnPlayerBoard(Integer playerIndex, HexCoordinates coordinates) {

    }

    @Override
    public Boolean checkIfThereAreActivePlayers() {
        return null;
    }

    @Override
    public Boolean checkIfPlayersAreWaitingForTile() {
        return null;
    }

    @Override
    public void pickNextTile() {

    }

    @Override
    public void endMatch() {

    }
}
