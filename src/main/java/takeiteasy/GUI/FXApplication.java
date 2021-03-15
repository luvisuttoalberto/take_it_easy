package takeiteasy.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import takeiteasy.GUI.Controller.IViewController;
import takeiteasy.game.IGame;

import java.io.IOException;

public class FXApplication extends Application implements IViewUpdater{

    private IGame game;
    private Stage stage;
    private IViewController ctrl;
    private IOContext currentContext = null;

    @Override
    public void start(Stage s) throws Exception {

        //TODO: Initialize game
        //TODO: Link game to networking?

        stage = s;
        updateView();

    }

    @Override
    public void updateView(){
        var json = game.getData();

        //TODO: take context from json
        IOContext iocontext = null;

        if (iocontext != currentContext){
            loadScene(iocontext);
        }
        ctrl.refreshView(json);
    }


    void loadScene(IOContext iocontext){

        String fxmlPath="";
        //TODO:do we want scene titles/sizes?
        switch (iocontext){
            case MainMenu -> fxmlPath = "main_menu.fxml";
            case LocalLobby -> fxmlPath = "local_lobby.fxml";
            case LocalMatch -> fxmlPath = "local_match.fxml";
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
