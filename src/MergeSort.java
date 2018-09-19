
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.HashMap;

public class MergeSort {
    private void merge(int[] arr, int l, int m, int r){
        int i; //index of L array
        int j; //index of R array
        int k; //index of original array

        int lNum = m-l+1;
        int rNum = r-m;

        int[] L = new int[lNum];
        int[] R = new int[rNum];

        for(i =0; i<lNum;i++){
            L[i]=arr[l+i];
        }

        for(j=0;j<rNum;j++){
            R[j]=arr[m+1+j];
        }

        i=0;j=0; k=l;

        while(i < lNum && j < rNum ){
            if(L[i]<=R[j]){
                arr[k] = L[i];
                i++;
            } else{
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while(i<lNum){
            arr[k] = L[i];
            i++;
            k++;
        }

        while(j<rNum){
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    private void mergeSort(int[] arr, int l, int r){
        if(l<r){
            int m = l+(r-l)/2;

            mergeSort(arr,l,m);
            mergeSort(arr,m+1,r);

            merge(arr,l,m,r);
        }
    }

    public void Sort(ArrayList<Rect> arr, int l, int r){
        HashMap<Integer,Integer> yToIndex = new HashMap<>();
        HashMap<Integer,Integer> xToIndex = new HashMap<>();

        int[] yVals = new int[arr.size()];
        int[] xVals = new int[arr.size()];


        //Initialize yToIndex
        for(int i =0; i<arr.size();i++){
            yVals[i] = arr.get(i).y;
            yToIndex.put(arr.get(i).y,i);
        }

        //Sort y values
        mergeSort(yVals,0,arr.size()-1);

        //Places all rectangles in sorted y order
        for(int i =0; i<arr.size();i++){
            arr.add(i,arr.remove(yToIndex.get(yVals[i]).intValue()));
        }
/*
        //Initialize xToIndex
        for(int i = 0; i < arr.size();i++){
            xVals[i] = arr.get(i).x;
            xToIndex.put(arr.get(i).x,i);
        }

        //Sort x values
        for(int i = 0; i<arr.size();i+=3){
            mergeSort(xVals,i,i+2);
        }

        //Places all rectangles in sorted xy order
        for(int i =0; i<arr.size();i++){
            arr.add(i,arr.remove(xToIndex.get(xVals[i]).intValue()));
        }*/
    }
}
