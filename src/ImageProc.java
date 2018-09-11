import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;

public class ImageProc {

    private int threshold = 0;
    private int shift = 5;
    private int bitsLeft = 8-shift;
    private int[] colors;
    private Size boundRect = new Size(250,250);


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
        Mat filtered = src.clone();

        for(int x = (int) (src.width()/2 - boundRect.width/2); x<src.size().width/2+boundRect.width/2; x++){
            for(int y = (int) (src.height()/2 - boundRect.width/2); y<src.size().height/2+boundRect.height/2; y++){
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
        //Clear Values
        for(int i =0; i< Math.pow(2,bitsLeft*3);i++)
            colors[i]=0;

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

        return (int) (Math.pow(r1-r2,2) + Math.pow(g1-g2,2) + Math.pow(b1-b2,2));
    }

    //Sets a color to a rubiks color index
    private void setColor(int color, int bestIndex) {
        colors[color] = bestIndex;
    }

    public synchronized void setThreshold(int threshold){
        if(this.threshold != threshold){
            this.threshold = threshold;
            fillValues();
        }
    }

    //Sets color, by index 1-6
    public void setRubiksColor(int index, int r, int g, int b){
        rubikColors[index] = new Scalar(b,g,r);
        fillValues();
    }

    public void drawBounds(Mat in){
        Point tl = new Point(in.width()/2 - boundRect.width/2,in.height()/2 - boundRect.height/2);
        Point br = new Point(in.width()/2 + boundRect.width/2,in.height()/2 + boundRect.height/2);

        Imgproc.rectangle(in,tl,br,new Scalar(0,0,255),3);
    }

}
