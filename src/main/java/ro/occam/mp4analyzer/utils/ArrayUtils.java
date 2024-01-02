package ro.occam.mp4analyzer.utils;

import java.util.Arrays;

public class ArrayUtils {

    public static byte[] getHead(byte[] array, int noOfElements) {
        return Arrays.copyOfRange(array, 0, noOfElements);
    }

    public static byte[] getTail(byte[] array, int noOfElements) {
        return Arrays.copyOfRange(array, array.length-noOfElements, array.length);
    }
}
