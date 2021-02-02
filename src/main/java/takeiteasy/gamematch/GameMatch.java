package takeiteasy.gamematch;

import takeiteasy.board.HexCoordinates;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;

import java.util.Arrays;
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
    public String[] getPlayerNames() {

        String[] playerNames = players.stream().map(IPlayer::getName).toArray(String[]::new);
        return playerNames;
    }

    private IPlayer retrievePlayerFromName(String playerName) throws PlayerNameNotFoundException {
        for (IPlayer p : players){
            if (p.getName() == playerName){
                return p;
            }
        }
        throw new PlayerNameNotFoundException(playerName);
    }

    @Override
    public void addPlayer(IPlayer player) throws PlayerWithSameNameCannotBeAddedException {
        try{
            retrievePlayerFromName(player.getName());
            throw new PlayerWithSameNameCannotBeAddedException(player.getName());
        }
        catch (PlayerNameNotFoundException e){
            players.add(player);
        }

    }

    @Override
    public void setPlayerName(String oldName, String newName) {

    }

    @Override
    public void removePlayer(String playerName) {

    }

    @Override
    public void startMatch() {

    }

    @Override
    public Tile getCurrentTile() {
        return null;
    }

    @Override
    public void positionCurrentTileOnPlayerBoard(String playerName, HexCoordinates coordinates) {

    }

    @Override
    public Boolean checkIfThereAreActivePlayers() {
        return null;
    }

    @Override
    public Boolean checkIfAllPlayersAreWaitingForTile() {
        return null;
    }

    @Override
    public void pickNextTile() {

    }

    @Override
    public void abortMatch() {

    }

    @Override
    public void endMatch() {

    }
}
