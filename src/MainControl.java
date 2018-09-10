import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;


public class MainControl {
    @FXML
    private ImageView raw, filtered, found;
    @FXML
    private Rectangle redDisplay,orangeDisplay,blueDisplay,greenDisplay,whiteDisplay,yellowDisplay;
    @FXML
    private Button startButton;
    @FXML
    private Slider threshold;

    //Stage Controls
    private Popup[] colorChooser = new Popup[6];
    private CustomPopup[] popupController = new CustomPopup[6];
    private Stage stage;
    private boolean canOpenPopup = true;
    private ImageProc imageProc;

    //Camera Controls
    private VideoCapture capture;
    private boolean cameraActive;
    private int cameraID =0; //-1 gives a device choosing dialog
    private Mat rawMat, filteredMat,foundMat;
    static Thread cameraT;
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public void initialize(){
        initializePopups();
        imageProc = new ImageProc();
        threshold.valueProperty().addListener((observable, oldValue, newValue) -> imageProc.setThreshold(newValue.intValue()));
        capture = new VideoCapture();
        cameraActive = false;
        startButton.setOnAction(event -> startCamera());
        cameraT = new Thread(()->{
            while (cameraActive) {
                doCamera();
            }
        });
        cameraT.setDaemon(true);
    }

    private void startCamera() {
        if (!cameraActive) {
            //Open Capture
            capture.open(cameraID);

            //Check if feed is live
            if (capture.isOpened()) {
                //Set Button Text and camera boolean
                startButton.setText("Stop Camera");
                cameraActive = true;
                cameraT.start();
            }
        } else {
            startButton.setText("Start Camera");
            cameraActive = false;
        }
    }

    private void doCamera() {
        Mat frame = new Mat();
        capture.read(frame);
        rawMat = frame;
        filteredMat = imageProc.filterImage(rawMat);

        raw.setImage(imageProc.matToImage(rawMat));
        filtered.setImage(imageProc.matToImage(filteredMat));
    }

    private void initializePopups() {
        for (int i = 0; i < 6; i++) {
            colorChooser[i] = new Popup();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(".\\layouts\\CustomPopup.fxml"));

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

                int r = (int) (popupController[finalI].getColor().getRed() * 255);
                int g = (int) (popupController[finalI].getColor().getGreen() * 255);
                int b = (int) (popupController[finalI].getColor().getBlue() * 255);

                switch (finalI){
                    case 0:
                        redDisplay.setFill(popupController[finalI].getColor());
                        imageProc.setColor(1,r,g,b);
                        break;
                    case 1:
                        orangeDisplay.setFill(popupController[finalI].getColor());
                        imageProc.setColor(4,r,g,b);
                        break;
                    case 2:
                        blueDisplay.setFill(popupController[finalI].getColor());
                        imageProc.setColor(3,r,g,b);
                        break;
                    case 3:
                        greenDisplay.setFill(popupController[finalI].getColor());
                        imageProc.setColor(2,r,g,b);
                        break;
                    case 4:
                        yellowDisplay.setFill(popupController[finalI].getColor());
                        imageProc.setColor(5,r,g,b);
                        break;
                    case 5:
                        whiteDisplay.setFill(popupController[finalI].getColor());
                        imageProc.setColor(6,r,g,b);
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