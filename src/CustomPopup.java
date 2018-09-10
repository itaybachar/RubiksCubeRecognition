import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

import java.text.NumberFormat;

public class CustomPopup {
    @FXML
    Rectangle colorDisplay;
    @FXML
    Slider rSlider,bSlider,gSlider;
    @FXML
    Button cancel,ok;
    @FXML
    TextField rText,gText,bText;

    private int R,G,B,tempR,tempG,tempB;

    @FXML
    HBox hbox;

    Popup popup;

    Double clickX,clickY;

    public void initialize(){
        R=B=G=0;
        sliderFilers();
        //Bind slider and text
        rText.textProperty().bindBidirectional(rSlider.valueProperty(),NumberFormat.getIntegerInstance());
        gText.textProperty().bindBidirectional(gSlider.valueProperty(),NumberFormat.getIntegerInstance());
        bText.textProperty().bindBidirectional(bSlider.valueProperty(),NumberFormat.getIntegerInstance());

        //Bind color display
        colorDisplayBind();

        hbox.setOnMousePressed(event -> {
           clickX = event.getSceneX();
           clickY = event.getSceneY();
        });

        hbox.setOnMouseDragged(event -> {
            popup.setX(event.getScreenX()-clickX);
            popup.setY(event.getScreenY()-clickY);

        });

        //Button Binding
        cancel.setOnAction(event -> {
            tempR = R;
            tempG = G;
            tempB = B;
            rSlider.setValue(tempR);
            gSlider.setValue(tempG);
            bSlider.setValue(tempB);
            popup.hide();

        });

        ok.setOnAction(event -> {
            R=tempR;
            G=tempG;
            B=tempB;
            popup.hide();
        });
    }

    public void setPopup(Popup popup){this.popup = popup;}

    private void updateDisplay(){
        colorDisplay.setFill(Color.rgb(tempR,tempG,tempB));
    }

    private void colorDisplayBind() {
        rText.textProperty().addListener((observable, oldValue, newValue) -> {
            tempR=Integer.parseInt(newValue);
            updateDisplay();
        });
        gText.textProperty().addListener((observable, oldValue, newValue) -> {
            tempG=Integer.parseInt(newValue);
            updateDisplay();
        });
        bText.textProperty().addListener((observable, oldValue, newValue) -> {
            tempB=Integer.parseInt(newValue);
            updateDisplay();
        });
    }

    private void sliderFilers() {
        //Set number Filter for textFields
        rText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                rText.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(rText.getText().isEmpty()){
                rText.setText("0");
            }
        });

        gText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                gText.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(gText.getText().isEmpty()){
                gText.setText("0");
            }
        });

        bText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bText.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(bText.getText().isEmpty()){
                bText.setText("0");
            }
        });

    }

    public void setColor(Color c){
        R = (int)(c.getRed()*255);
        G = (int)(c.getGreen()*255);
        B = (int)(c.getBlue()*255);
        rSlider.setValue(R);
        gSlider.setValue(G);
        bSlider.setValue(B);
        updateDisplay();
    }

    public Color getColor(){
        return Color.rgb(R,G,B);
    }

}
