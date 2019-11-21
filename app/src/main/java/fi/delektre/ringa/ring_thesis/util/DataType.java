package fi.delektre.ringa.ring_thesis.util;

public class DataType {

    public static final int HEIGHT = 0;
    public static final int WEIGHT = 1;
    public static final int BIRTHDAY = 2;
    public static final int GENDER = 3;
    public static final int NAME = 4;

    private int correspondingInt;

    public static final String DataLabel[] = new String[]{"Height", "Weight", "Birthday", "Gender", "Name"};

    DataType(int i) {
        this.correspondingInt = i;
    }

    public int getCorrespondingInt() {
        return correspondingInt;
    }
}
