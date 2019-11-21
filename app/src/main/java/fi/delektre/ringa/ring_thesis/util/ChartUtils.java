package fi.delektre.ringa.ring_thesis.util;

import com.github.mikephil.charting.data.DataSet;

public class ChartUtils {
    private static final int MAX_ENTRIES = 50;
    public static void removeOutdatedEntries(DataSet... dataSets){
        for (DataSet ds : dataSets){
            while (ds.getEntryCount() > MAX_ENTRIES){
                ds.removeFirst();
            }
        }

    }
}
