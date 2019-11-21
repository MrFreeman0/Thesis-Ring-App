package fi.delektre.ringa.ring_thesis.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;

import fi.delektre.ringa.ring_thesis.RecyclerSelectorView;
import fi.delektre.ringa.ring_thesis.RecyclerSelectorView_;
import fi.delektre.ringa.ring_thesis.util.SelectorOption;

@EBean
public class SelectorRecyclerViewAdapter extends RecyclerViewAdapterBase<SelectorOption, RecyclerSelectorView> {
    private ArrayList<SelectorOption> mSelectorOptions = new ArrayList<>();
    public Context mContext;
    public ArrayList<ILineDataSet> lineDataSetsTemperatures;
    public LineDataSet lineDataSetTemperature1;
    public LineDataSet lineDataSetTemperature2;
    public LineData tempData;
    public LineChart lineChart;
    //public int graphIndex;
    public int buttonPosition;
    public LineDataSet lineDataSetBloodPressure;
    public LineDataSet lineDataSetPulse;
    public LineDataSet lineDataSetFluidBalance;
    ArrayList<Entry> yAxesTemp1 = new ArrayList<>();
    ArrayList<Entry> yAxesTemp2 = new ArrayList<>();
    ArrayList<Entry> yAxesPulse = new ArrayList<>();
    ArrayList<Entry> yAxesFluid = new ArrayList<>();
    ArrayList<Entry> yAxesPressure = new ArrayList<>();

    @RootContext
    Context context;


    @Override
    protected RecyclerSelectorView onCreateItemView(ViewGroup parent, int ViewType) {
        return RecyclerSelectorView_.build(context);
    }

    public void initGraphsDataSets(){
        lineDataSetBloodPressure = new LineDataSet(yAxesPressure, "Blood Pressure");
        lineDataSetBloodPressure.setDrawCircles(false);
        lineDataSetBloodPressure.setColor(Color.BLUE);
        lineDataSetBloodPressure.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        
        lineDataSetPulse = new LineDataSet(yAxesPulse, "Pulse");
        lineDataSetPulse.setDrawCircles(false);
        lineDataSetPulse.setColor(Color.RED);
        lineDataSetPulse.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lineDataSetFluidBalance = new LineDataSet(yAxesFluid, "Fluid Balance");
        lineDataSetFluidBalance.setDrawCircles(false);
        lineDataSetFluidBalance.setColor(Color.GREEN);
        lineDataSetFluidBalance.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lineDataSetsTemperatures = new ArrayList<>();
        lineDataSetTemperature1 = new LineDataSet(yAxesTemp1, "Temp 1");
        lineDataSetTemperature2 = new LineDataSet(yAxesTemp2, "Temp 2");
        lineDataSetTemperature1.setDrawCircles(false);
        lineDataSetTemperature2.setDrawCircles(false);
        lineDataSetTemperature1.setColor(Color.RED);
        lineDataSetTemperature1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSetTemperature2.setColor(Color.BLUE);
        lineDataSetTemperature2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSetsTemperatures.add(lineDataSetTemperature1);
        lineDataSetsTemperatures.add(lineDataSetTemperature2);
        lineDataSetsTemperatures.set(0, lineDataSetTemperature1);
        lineDataSetsTemperatures.set(1, lineDataSetTemperature2);


    }

    @Override
    public void onBindViewHolder(ViewWrapper<RecyclerSelectorView> viewHolder, int position) {
        RecyclerSelectorView view = viewHolder.getView();
        SelectorOption option = items.get(position);
        view.bind(option);
        int pos = position;
        
        view.setOnClickListener(view1 -> {
            if (pos == 0){
                buttonPosition = 0;
                lineChart.setData(new LineData(lineDataSetsTemperatures));
            }
            if (pos == 1){
                buttonPosition = 1;
                lineChart.setData(new LineData(lineDataSetFluidBalance));
            }
            if (pos == 2){
                buttonPosition = 2;
                lineChart.setData(new LineData(lineDataSetPulse));
            }
            if (pos == 3){
                buttonPosition = 3;
                lineChart.setData(new LineData(lineDataSetBloodPressure));
            }
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

        });
    }
}
