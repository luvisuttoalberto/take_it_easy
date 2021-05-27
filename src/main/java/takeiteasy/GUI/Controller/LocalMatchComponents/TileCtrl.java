package takeiteasy.GUI.Controller.LocalMatchComponents;

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TileCtrl extends AnchorPane {

    final Color COLOR_EMPTY = Color.WHITE;
    final Color COLOR_PLACED = Color.color(1.,.757,.357);
    final Color COLOR_FOCUS = Color.YELLOW;

    public Polygon graphic_hitBox;
    Polygon graphic_bg;
    Text text_top, text_left, text_right;

    public TileCtrl(double width, double height) {
        double hunit=width*.25, vunit=height*.5;
        buildContent(hunit,vunit);
    }

    void setValues(Integer top, Integer left, Integer right){
        text_top.setText(top.toString());
        text_left.setText(left.toString());
        text_right.setText(right.toString());
    }

    public void setPlacedGraphics(Integer top, Integer left, Integer right){
        graphic_bg.setFill(COLOR_PLACED);
        setValues(top, left, right);
    }

    public void setFocusedGraphics(Integer top, Integer left, Integer right) {
        graphic_bg.setFill(COLOR_FOCUS);
        setValues(top, left, right);
    }

    public void resetGraphics(){
        graphic_bg.setFill(COLOR_EMPTY);
        text_top.setText("");
        text_left.setText("");
        text_right.setText("");
    }

    void buildContent(double hunit,double vunit){

        graphic_bg = new Polygon();

        //  01234
        //0 .6.5.
        //1 1...4
        //2 .2.3.
        graphic_bg.getPoints().setAll(
                0.0,    vunit,
                hunit,  2*vunit,
                3*hunit,2*vunit,
                4*hunit,vunit,
                3*hunit,0.0,
                hunit,  0.0
                );

        graphic_bg.setStroke(Color.BLACK);

        // hitbox is 1px inset wrt bg
        graphic_hitBox = new Polygon();
        graphic_hitBox.getPoints().setAll(
                0.0     +1.0,   vunit   ,
                hunit   +1.0,   2*vunit -1.0,
                3*hunit -1.0,   2*vunit -1.0,
                4*hunit -1.0,   vunit   ,
                3*hunit -1.0,   0.0     +1.0,
                hunit   +1.0,   0.0     +1.0
        );
        graphic_hitBox.setFill(Color.TRANSPARENT);

        text_top = new Text();
        text_left = new Text();
        text_right = new Text();

        text_top.setTextAlignment(TextAlignment.CENTER);
        text_left.setTextAlignment(TextAlignment.CENTER);
        text_right.setTextAlignment(TextAlignment.CENTER);

        resetGraphics();

        HBox layout_text_top = new HBox();
        layout_text_top.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(layout_text_top, Priority.ALWAYS);
        layout_text_top.getChildren().add(text_top);

        HBox layout_text_bot_l = new HBox();
        layout_text_bot_l.setAlignment(Pos.BOTTOM_LEFT);
        HBox.setHgrow(layout_text_bot_l, Priority.ALWAYS);
        layout_text_bot_l.getChildren().add(text_left);

        HBox layout_text_bot_r = new HBox();
        layout_text_bot_r.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setHgrow(layout_text_bot_r, Priority.ALWAYS);
        layout_text_bot_r.getChildren().add(text_right);

        HBox layout_text_bot = new HBox();
        layout_text_bot.setAlignment(Pos.CENTER);
        VBox.setVgrow(layout_text_bot, Priority.ALWAYS);
        layout_text_bot.getChildren().addAll(layout_text_bot_l,layout_text_bot_r);

        VBox layout_text = new VBox();
        layout_text.setAlignment(Pos.CENTER);
        layout_text.getChildren().addAll(layout_text_top,layout_text_bot);


        this.getChildren().addAll(graphic_bg,layout_text, graphic_hitBox);

        setTopAnchor(layout_text,vunit*.1);
        setBottomAnchor(layout_text,vunit*.5);
        setLeftAnchor(layout_text,hunit*.9);
        setRightAnchor(layout_text,hunit*.9);
    }
}
