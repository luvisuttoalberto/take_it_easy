package takeiteasy.gamematch;

public class PlayerWithSameNameCannotBeAddedException extends Exception {
    public PlayerWithSameNameCannotBeAddedException(String playerName) {
        super(playerName);
    }
}
