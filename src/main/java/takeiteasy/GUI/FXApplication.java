package takeiteasy.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import takeiteasy.GUI.Controller.IViewController;
import takeiteasy.JSONKeys;
import takeiteasy.game.Game;
import takeiteasy.game.IGame;

import java.io.IOException;

public class FXApplication extends Application implements IViewUpdater{

    enum IOContext {
        MainMenu,
        LocalLobby,
        LocalMatch,
    }

    IGame game;
    Stage stage;
    IViewController currentViewCtrl;
    IOContext currentContext = null;

    @Override
    public void start(Stage s) throws Exception {

        game = new Game();

        stage = s;
        stage.setResizable(false);
        stage.setTitle("Take it Easy");

        updateView();

    }

    IOContext getIOContextFromGameData(JSONObject gameData){
        String gameState = gameData.getString(JSONKeys.GAME_STATE);
        if(gameState.equals(IGame.State.LOCAL_LOBBY.name())){
            return IOContext.LocalLobby;
        }
        else if(gameState.equals(IGame.State.LOCAL_MATCH.name())){
            return IOContext.LocalMatch;
        }
        return IOContext.MainMenu;
    }

    @Override
    public void updateView(){
        JSONObject gameData = game.getData();

        IOContext newContext = getIOContextFromGameData(gameData);

        // Load new scene if it changed
        if (newContext != currentContext){
            loadScene(newContext);
        }

        // refresh current scene in any case
        currentViewCtrl.refreshView(gameData);
    }


    String getFxmlPathFromIOContext(IOContext iocontext){
        switch (iocontext){
            case LocalLobby : return "/fxml/local_lobby.fxml";
            case LocalMatch : return "/fxml/local_match.fxml";
            default : return "/fxml/main_menu.fxml";
        }
    }

    void loadScene(IOContext iocontext){

        FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlPathFromIOContext(iocontext)));
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(node);

        currentViewCtrl = loader.getController();
        currentViewCtrl.injectGame(game);
        currentViewCtrl.injectViewUpdater(this);

        currentContext = iocontext;

        stage.setScene(scene);
        stage.show();
    }

}
