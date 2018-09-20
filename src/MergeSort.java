import org.opencv.core.Rect;
import java.util.ArrayList;


public class MergeSort {

    ArrayList<Rect> R = new ArrayList<>(9);
    ArrayList<Rect> L = new ArrayList<>(9);


    private void merge(ArrayList<Rect> arr, int l, int m, int r,boolean isYSort){
        int i; //index of L array
        int j; //index of R array
        int k; //index of original array

        int lNum = m-l+1;
        int rNum = r-m;

        for(i =0; i<lNum;i++){
            L.add(arr.get(l+i));
        }

        for(j=0;j<rNum;j++){
            R.add(arr.get(m+1+j));
        }

        i=0;j=0; k=l;

        if(isYSort) {
            while (i < lNum && j < rNum) {
                if (L.get(i).y <= R.get(j).y) {
                    arr.set(k, L.get(i));
                    i++;
                } else {
                    arr.set(k, R.get(j));
                    j++;
                }
                k++;
            }
        } else {
            while (i < lNum && j < rNum) {
                if (L.get(i).x <= R.get(j).x) {
                    arr.set(k, L.get(i));
                    i++;
                } else {
                    arr.set(k, R.get(j));
                    j++;
                }
                k++;
            }
        }

        while(i<lNum){
            arr.set(k,L.get(i));
            i++;
            k++;
        }

        while(j<rNum){
            arr.set(k,R.get(j));
            j++;
            k++;
        }
        R.clear();
        L.clear();
    }

    private void mergeSort(ArrayList<Rect> arr, int l, int r,boolean isYSort){
        if(l<r){
            int m = l+(r-l)/2;

            mergeSort(arr,l,m,isYSort);
            mergeSort(arr,m+1,r,isYSort);

            merge(arr,l,m,r,isYSort);
        }
    }

    void Sort(ArrayList<Rect> arr, int l, int r) {
        //Sort by y
        mergeSort(arr,l,r,true);

        //Sort By x
        while(l<r){
            mergeSort(arr,l,l+2,false);
            l+=3;
        }
    }
}
