package layouts;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;


public class MainControl {
    @FXML
    private ImageView raw, filtered, found;
    @FXML
    private Rectangle redDisplay,orangeDisplay,blueDisplay,greenDisplay,whiteDisplay,yellowDisplay;
    @FXML
    private Button startButton;

    private Popup[] colorChooser = new Popup[6];
    private CustomPopup[] popupController = new CustomPopup[6];
    private Stage stage;

    private boolean canOpenPopup = true;

    public void initialize(){

          initializePopups();

    }

    private void initializePopups() {

        for (int i = 0; i < 6; i++) {
            colorChooser[i] = new Popup();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomPopup.fxml"));

            try {
                colorChooser[i].getContent().add(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }

            popupController[i] = loader.getController();
            popupController[i].setPopup(colorChooser[i]);

            colorChooser[i].setOnShown(event -> canOpenPopup=false);
            int finalI = i;
            colorChooser[i].setOnHidden(event -> {
                canOpenPopup=true;

                switch (finalI){
                    case 0:
                        redDisplay.setFill(popupController[finalI].getColor());
                        break;
                    case 1:
                        orangeDisplay.setFill(popupController[finalI].getColor());
                        break;
                    case 2:
                        blueDisplay.setFill(popupController[finalI].getColor());
                        break;
                    case 3:
                        greenDisplay.setFill(popupController[finalI].getColor());
                        break;
                    case 4:
                        yellowDisplay.setFill(popupController[finalI].getColor());
                        break;
                    case 5:
                        whiteDisplay.setFill(popupController[finalI].getColor());
                        break;
                }


            });
        }



        redDisplay.setOnMouseClicked(event -> {
            if (canOpenPopup)
                colorChooser[0].show(stage);
        });
        orangeDisplay.setOnMouseClicked(event -> {
            if (canOpenPopup)
                colorChooser[1].show(stage);
        });
        blueDisplay.setOnMouseClicked(event -> {
            if (canOpenPopup)
                colorChooser[2].show(stage);
        });
        greenDisplay.setOnMouseClicked(event -> {
            if (canOpenPopup)
                colorChooser[3].show(stage);
        });
        yellowDisplay.setOnMouseClicked(event -> {
            if (canOpenPopup)
                colorChooser[4].show(stage);
        });
        whiteDisplay.setOnMouseClicked(event -> {
            if (canOpenPopup)
                colorChooser[5].show(stage);
        });

        //Set colors
        popupController[0].setColor(Color.RED);
        popupController[1].setColor(Color.ORANGE);
        popupController[2].setColor(Color.BLUE);
        popupController[3].setColor(Color.GREEN);
        popupController[4].setColor(Color.YELLOW);
        popupController[5].setColor(Color.WHITE);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
