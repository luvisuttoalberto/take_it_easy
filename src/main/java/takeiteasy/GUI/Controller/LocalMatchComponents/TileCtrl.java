package takeiteasy.GUI.Controller.LocalMatchComponents;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.json.JSONObject;

//TODO: see if this needs to be initializable as the localmatchctrl
public class TileCtrl extends AnchorPane {

    public Polygon hitBox;
    Polygon bg;
    Text text_top, text_left, text_right;

    public TileCtrl() {
//        setStyle();
//        hitBox.setOnMouseEntered();
        buildContent();
    }

    //TODO: is private ok?
    void setValues(Integer top, Integer left, Integer right){
        text_top.setText(top.toString());
        text_left.setText(left.toString());
        text_right.setText(right.toString());
    }

    public void setPlacedGraphics(Integer top, Integer left, Integer right){
        bg.setFill(Color.AQUA);
        setValues(top, left, right);
    }

    public void setFocusedGraphics(Integer top, Integer left, Integer right) {
        bg.setFill(Color.RED);
        setValues(top, left, right);
    }

    public void resetGraphics(){
        bg.setFill(Color.WHITE);
        text_top.setText("");
        text_left.setText("");
        text_right.setText("");
    }

    void buildContent(){
        bg = new Polygon();

        //TODO: determine units. v = h*sqrt(3) ~ h*1.732
        double hunit=20, vunit=34;
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

        bg.setFill(Color.WHITE);
        bg.setStroke(Color.BLACK);

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
        hitBox.setFill(Color.TRANSPARENT);

        text_top = new Text();
        text_left = new Text();
        text_right = new Text();

        //TODO: init to empty, position
        resetGraphics();

        this.getChildren().addAll(bg,text_top,text_left,text_right, hitBox);

        //TODO: fix these magic numbers
        setTopAnchor(text_top, -vunit*0.9);
        setLeftAnchor(text_top, -3.0);
        setTopAnchor(text_left, vunit*0.2);
        setLeftAnchor(text_left, -hunit*1.2);
        setTopAnchor(text_right, vunit*0.2);
        setLeftAnchor(text_right, hunit*0.9);
    }
}
