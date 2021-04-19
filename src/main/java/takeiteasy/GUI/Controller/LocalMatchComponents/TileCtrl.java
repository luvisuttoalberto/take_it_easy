package takeiteasy.GUI.Controller.LocalMatchComponents;

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

    //TODO: test
    public void setValues(Integer top, Integer left, Integer right){
        text_top.setText(top.toString());
        text_left.setText(left.toString());
        text_right.setText(right.toString());
    }

    void buildContent(){
        bg = new Polygon();

        //TODO: determine units. v = h*sqrt(3) ~ h*1.732
        double hunit=5, vunit=5;
        //+1 .6.5.
        // 0 1...4
        //-1 .2.3.
        //   21012
        //   --.++

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

        //TODO: init to empty, position
        text_top.setText("T");
        text_left.setText("L");
        text_right.setText("R");

        this.getChildren().addAll(bg,text_top,text_left,text_right, hitBox);
    }

}
