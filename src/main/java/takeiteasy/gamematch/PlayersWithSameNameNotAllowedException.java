package takeiteasy.gamematch;

public class PlayersWithSameNameNotAllowedException extends Exception {
    public PlayersWithSameNameNotAllowedException(String playerName) {
        super(playerName);
    }
}
