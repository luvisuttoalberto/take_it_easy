package takeiteasy.core.gamematch.exceptions;

public class PlayersWithSameNameNotAllowedException extends Exception {
    public PlayersWithSameNameNotAllowedException(String playerName) {
        super(playerName);
    }
}
