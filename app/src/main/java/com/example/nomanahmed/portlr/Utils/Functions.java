package com.example.nomanahmed.portlr.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Functions {

    public static ArrayList<String> sortArray(List<String> args) {
        String[] myArray = new String[args.size()];
        for (int j = 0; j < args.size(); j++) {
            myArray[j] = args.get(j);
        }

        int size = myArray.length;

        for(int i = 0; i<size-1; i++) {
            for (int j = i+1; j<myArray.length; j++) {
                if(myArray[i].compareTo(myArray[j])>0) {
                    String temp = myArray[i];
                    myArray[i] = myArray[j];
                    myArray[j] = temp;
                }
            }
        }
        return new ArrayList<>(Arrays.asList(myArray));
    }

}
