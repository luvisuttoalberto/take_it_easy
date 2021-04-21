package takeiteasy.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.IViewController;
import takeiteasy.game.Game;
import takeiteasy.game.IGame;

import java.io.IOException;

public class FXApplication extends Application implements IViewUpdater{

    enum IOContext {
        MainMenu,
        LocalLobby,
        LocalMatch
    }

    private IGame game;
    private Stage stage;
    private IViewController currentViewCtrl;
    private IOContext currentContext = null;

    @Override
    public void start(Stage s) throws Exception {

        // Initialize game
        game = new Game();
        directlyToLocalMatch(game);
        //TODO: Link game to networking?

        stage = s;
        updateView();

    }

    @Override
    public void updateView(){
        JSONObject gameData = game.getData();

        // take context from json
        IOContext newContext = null;
        String gs = gameData.getString("gameState");
        if(gs == IGame.State.MAIN_MENU.name()){
            newContext = IOContext.MainMenu;
        }
        else if(gs == IGame.State.LOCAL_LOBBY.name()){
            newContext = IOContext.LocalLobby;
        }
        else if(gs == IGame.State.LOCAL_MATCH.name()){
            newContext = IOContext.LocalMatch;
        }
        else{
            //DEBUG:
            System.out.println("Invalid game state: " + gs);
            return;
        }

        // Load new scene if it changed
        if (newContext != currentContext){
            loadScene(newContext);
        }

        // refresh current scene in any case
        currentViewCtrl.refreshView(gameData);
    }


    void loadScene(IOContext iocontext){

        String fxmlPath="";
        //TODO:do we want scene titles/sizes?
        switch (iocontext){
            //TODO: change : back to -> if this doesn't work BEFORE MERGING!!!!!!
            case MainMenu : fxmlPath = "/fxml/main_menu.fxml";
            case LocalLobby : fxmlPath = "/fxml/local_lobby.fxml";
            case LocalMatch : fxmlPath = "/fxml/local_match.fxml";
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent node = null;
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
            //TODO: is this right?
        }

        Scene scene = new Scene(node);

        currentViewCtrl = loader.getController();
        currentViewCtrl.injectGame(game);
        currentViewCtrl.injectViewUpdater(this);

        currentContext = iocontext;

        stage.setTitle("Take it Easy");
        stage.setScene(scene);
        stage.show();
    }

    private void directlyToLocalMatch(IGame game){
        game.createLocalLobby();
        game.addPlayer("Dario");
        game.startLocalMatch();
    }

}
