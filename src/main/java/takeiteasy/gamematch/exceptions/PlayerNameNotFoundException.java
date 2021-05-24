package takeiteasy.gamematch.exceptions;

public class PlayerNameNotFoundException extends Exception {
    public PlayerNameNotFoundException(String playerName) {
        super(playerName);
    }
}
