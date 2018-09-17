import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageProc {

    private int threshold = 0;
    private int shift = 5;
    private int bitsLeft = 8-shift;
    private int[] colors;
    private Size boundRect = new Size(250,250);

    private Scalar[] rubikColors = {
            new Scalar(0,0,0), //Black
            new Scalar(0,0,220), //Red
            new Scalar(0,195,0), //Green
            new Scalar(255,0,0), //Blue
            new Scalar(0,85,255), //Orange
            new Scalar(0,210,210), //Yellow
            new Scalar(255,255,255) //White
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

    private int bgrToInt(byte[] color) {
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

    public void findCountours(Mat src,Mat dest,Mat found) {
        ArrayList<Rect> rects = new ArrayList<>();
        ArrayList<Scalar> colors = new ArrayList<>();

        //Cycle through all rubiks colors
        for (int i = 1; i < rubikColors.length; i++) {
            Mat mask = new Mat();

            Core.inRange(src, rubikColors[i], rubikColors[i], mask);
            Imgproc.blur(mask,mask,new Size(6,6));
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat edges = new Mat();
            Imgproc.Canny(mask, edges, 100, 300);
            Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            MatOfPoint2f curve = new MatOfPoint2f();
            MatOfPoint2f approxCurve = new MatOfPoint2f();

            for (MatOfPoint contour : contours) {
                Rect rect;
                curve.fromList(contour.toList());
                Imgproc.approxPolyDP(curve, approxCurve, Imgproc.arcLength(curve, true) * 0.02, true);

                MatOfPoint points = new MatOfPoint(approxCurve.toArray());

                rect = Imgproc.boundingRect(points);
                if (Math.abs(1 - (double) rect.height / rect.width) <= 0.3 &&
                        Math.abs(1 - (double) rect.width / rect.height) <= 0.3 && rect.area()>3000) {
                    colors.add(rubikColors[i]);
                    rects.add(rect);
                }
            }
        }


        cleanUpRects(rects);
        found.setTo(new Scalar(45,45,45));
        for(int i = 0; i<rects.size();i++){
            if(rects.size()==9)
           Imgproc.rectangle(found, rects.get(i).tl(), rects.get(i).br(), rubikColors[colorForRect(rects.get(i),src)], 3);
        }


        if(rects.size() == 9){
          //  rectsToCube(src,found,rects,colors);
        }
    }

    private void rectsToCube(Mat src, Mat dst, ArrayList<Rect> rects, ArrayList<Scalar> colors){
       dst.setTo(new Scalar(45,45,45));

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j<3; j++)
            {
                Point tl = new Point(dst.width()/2-boundRect.width/2 +10,dst.height()/2-boundRect.height/2 + 10);
                Point br = new Point(dst.width()/2+boundRect.width/2+10,dst.height()/2+boundRect.height/2+10);
                Imgproc.rectangle(dst,tl,br,new Scalar(255,255,255),4);
            }
        }

        for(int i = 0; i< rects.size();i++){
            Imgproc.rectangle(dst,rects.get(i).tl(),rects.get(i).br(),colors.get(i),3);
        }
    }

    public boolean rectIntersection(Rect r1, Rect r2){
       int top = Math.max(r1.y,r2.y);
       int bottom = Math.min(r1.y+r1.height,r2.y+r2.height);
       int left = Math.max(r1.x,r2.x);
       int right = Math.min(r1.x+r1.width,r2.x+r2.width);

       if(right-left>0 && bottom-top>0){
           return true;
       } else return false;
    }

    private void cleanUpRects(ArrayList<Rect> rects){
        ArrayList<Rect> tempRects = new ArrayList<>();
        for(int i = 0; i<rects.size(); i++) {
            boolean bad = false;
            for (int j = 0; j < rects.size(); j++) {
                if (i != j) {
                    if (rectIntersection(rects.get(i), rects.get(j))) {
                        if (rects.get(i).area() > rects.get(j).area()) {
                            bad = true;
                        }
                    }
                }
            }
            if (!bad) {
                tempRects.add(rects.get(i));
            }
        }
        rects = tempRects;
    }

    private int colorForRect(Rect rect, Mat src){
        int[] colorCount = new int[7];
            for(int x = rect.x; x<rect.x+rect.width;x++){
                for(int y= rect.y;y<rect.y+rect.height;y++){
                    double[] bgr = src.get(y,x);
                    for(int j = 0; j<7; j++){
                        if(bgrToInt(scalarToArray(rubikColors[j]))== bgrToInt(bgr)){
                            colorCount[j]++;
                            break;
                        }
                    }
                }

        }

        int bestIndex = -1; int best = -100;
        for(int i =0; i<7;i++){
            if(colorCount[i]>best){
                best = colorCount[i];
                bestIndex=i;
            }
        }

        return bestIndex;
    }

}
