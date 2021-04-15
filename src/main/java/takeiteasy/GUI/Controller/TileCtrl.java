package takeiteasy.GUI.Controller;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

public class TileCtrl extends Pane {

    Polygon hitBox, bg;
    Text text_top, text_left, text_right;

    public TileCtrl() {
//        setStyle();
//        hitBox.setOnMouseEntered();
        buildContent();
    }
    void buildContent(){
        bg = new Polygon();

        //TODO: determine units
        double hunit=5, vunit=5;
        //2 .5.4.
        //1 6...3
        //0 .1.2.
        //  01234

        bg.getPoints().setAll(
                -hunit*2,0.0,
                -hunit,-vunit,
                hunit,-vunit,
                hunit*2,0.0,
                hunit,vunit,
                -hunit,vunit
        );

        // hitbox is 1px inset wrt bg
        hitBox = new Polygon();
        hitBox.getPoints().setAll(
                -hunit*2+1,0.0,
                -hunit+1,-vunit+1,
                hunit-1,-vunit+1,
                hunit*2-1,0.0,
                hunit-1,vunit-1,
                -hunit+1,vunit-1
        );
        hitBox.setVisible(false);

        text_top = new Text();
        text_left = new Text();
        text_right = new Text();

        //TODO: init to empty
        text_top.setText("T");
        text_left.setText("L");
        text_right.setText("R");

        this.getChildren().addAll(bg,text_top,text_left,text_right, hitBox);
    }

}
