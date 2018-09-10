import javafx.scene.image.Image;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;

public class ImageProc {

    private int threshold = 0;
    private int shift = 4;
    private int bitsLeft = 8-shift;
    private int[] colors;


    private Scalar[] rubikColors = {
            new Scalar(0,0,0), //Black
            new Scalar(0,0,255), //Red
            new Scalar(0,240,0), //Green
            new Scalar(255,0,0), //Blue
            new Scalar(0,90,255), //Orange
            new Scalar(0,200,200), //Yellow
            new Scalar(200,200,200) //White
    };

    public ImageProc(){
        colors = new int[(int)Math.pow(2,bitsLeft*3)];
        fillValues();
    }

    //Filters an Image to reduced colors
    public synchronized Mat filterImage(Mat src){
        Mat filtered = new Mat(src.size(),CvType.CV_8UC3);

        for(int x = 0; x<src.size().width; x++){
            for(int y = 0; y<src.size().height; y++){
                double[] bgr = src.get(y,x);

                int index = bgrToInt(bgr);
                int colorIndex = colors[index];
                filtered.put(y,x,scalarToArray(rubikColors[colorIndex]));
            }
        }

        return filtered;
    }

    //Converts a color array to a reduced color Int
    private int bgrToInt(double[] color) {
        int b = (int) (color[0]) >> shift;
        int g = (int)(color[1]) >> shift;
        int r = (int)(color[2]) >> shift;
        return (b << bitsLeft*2 | g << bitsLeft | r);
    }

    //Scalar into Mat friendly array
    private byte[] scalarToArray(Scalar rubikColor) {
        return new byte[] {
                (byte)(rubikColor.val[0]),
                (byte)(rubikColor.val[1]),
                (byte)(rubikColor.val[2])
        };
    }

    //Converts Mat to an Image
    public Image matToImage(Mat source){
        MatOfByte buffer = new MatOfByte();
        ByteArrayInputStream inputStream;

        Imgcodecs.imencode(".png", source, buffer);
        inputStream = new ByteArrayInputStream(buffer.toArray());
        return new Image(inputStream);
    }

    //Fills the array of reduced color spectrum.
    private synchronized void fillValues(){
        System.out.println(threshold);
        //Cycle through all possible colors
        for(int b = 0; b<Math.pow(2,bitsLeft);b++) {
            for (int g = 0; g < Math.pow(2,bitsLeft); g++) {
                for (int r = 0; r < Math.pow(2,bitsLeft); r++) {

                    //Current Color index
                    int color = b << bitsLeft*2 | g << bitsLeft| r;

                    //Set minimum color distance to red dist.
                    int minDistance = difference(b,g,r,rubikColors[1]);
                    int bestIndex = 1;

                    //Check if the other colors are closer
                    for(int i = 2; i<7;i++){
                        int currDistance = difference(b,g,r,rubikColors[i]);

                        //Update minDist and bestIndex accordingly
                        if(currDistance<minDistance) {
                            minDistance = currDistance;
                            bestIndex = i;
                        }
                    }

                    //Setting color if distance is good
                    if(minDistance< threshold){
                        setColor(color,bestIndex);
                    }
                }
            }
        }
    }

    //Difference between two colors
    private int difference(int b1, int g1,int r1,Scalar color){
        int b2 = (int)(color.val[0])>>shift;
        int g2 = (int)(color.val[1])>>shift;
        int r2 = (int)(color.val[2])>>shift;

        return Math.abs(r1-r2) + Math.abs(g1-g2) + Math.abs(b1-b2);
    }

    //Sets a color to a rubiks color index
    private void setColor(int color, int bestIndex) {
        colors[color] = bestIndex;
    }

    public synchronized void setThreshold(int threshold){
        if(this.threshold != threshold){
            this.threshold = threshold;
            colors = new int[(int)Math.pow(2,bitsLeft*3)];
            fillValues();
        }
    }

    //Sets color, by index 1-6
    public void setColor(int index, int r, int g, int b){
        rubikColors[index] = new Scalar(b,g,r);
    }
}
