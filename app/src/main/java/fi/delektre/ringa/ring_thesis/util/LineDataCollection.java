package fi.delektre.ringa.ring_thesis.util;

import com.github.mikephil.charting.data.LineData;
public class LineDataCollection {

    private LineData temp;
    private LineData pressure;

    public LineDataCollection(){
        this.temp = new LineData();
        this.pressure = new LineData();
    }

    public LineDataCollection(LineData temp, LineData pressure){
        this.temp = temp;
        this.pressure = pressure;
    }

    public void setTemp(LineData temp){
        this.temp = temp;
    }

    public void setPressure(LineData pressure){
        this.pressure = pressure;
    }


    public LineData getPressure() {
        return pressure;
    }

    public LineData getTemp() {
        return temp;
    }
}
