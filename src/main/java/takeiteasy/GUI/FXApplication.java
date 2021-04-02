package takeiteasy.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    private IViewController ctrl;
    private IOContext currentContext = null;

    @Override
    public void start(Stage s) throws Exception {

        // Initialize game
        game = new Game();
        //TODO: Link game to networking?

        stage = s;
        updateView();

    }

    @Override
    public void updateView(){
        var json = game.getData();

        // take context from json
        IOContext newContext = null;
        String gs = json.getString("gameState");
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

        if (newContext != currentContext){
            loadScene(newContext);
        }
        ctrl.refreshView(json);
    }


    void loadScene(IOContext iocontext){

        String fxmlPath="";
        //TODO:do we want scene titles/sizes?
        switch (iocontext){
            case MainMenu -> fxmlPath = "/fxml/main_menu.fxml";
            case LocalLobby -> fxmlPath = "/fxml/local_lobby.fxml";
            case LocalMatch -> fxmlPath = "/fxml/local_match.fxml";
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

        Scene scene = new Scene(node, 600, 400);

        ctrl = loader.getController();
        ctrl.injectGame(game);
        ctrl.injectViewUpdater(this);

        currentContext = iocontext;

        stage.setTitle("Take it Easy");
        stage.setScene(scene);
        stage.show();
    }

}