package fi.delektre.ringa.ring_thesis;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@EActivity (R.layout.activity_user_history)
public class UserHistory extends AppCompatActivity {
    //View declaration
    @ViewById(R.id.tabs_history)
    protected TabLayout tabLayout;

    @ViewById(R.id.history_linechart)
    protected LineChart historyChart;

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference mReferecence;
    private FirebaseDatabase mFirebaseDatabase;
    private String UserID;

    //Android MPChart variables
    private int graphIndex;
    public LineDataSet dataToday;
    public LineDataSet dataWeek;
    public LineDataSet dataMonth;

    //Variables to store averages
    private int dailyAverage;

    //Variables to obtain data over day/week/month
    private long currentDayReference;
    private long currentDayMillis;
    private long currentDayBeginning;
    private long currentDayEnding;
    private long dayToMillis;
    private long weekToMillis;
    private long monthToMillis;
    private long timestampMillis;

    ArrayList<Integer>[] hourMeasurementsInDay;
    private int hourlyAverages[];

    //For debug purposes
    private static final String TAG = "ActivityUserHistory";


    @AfterViews
    void selectTabBehavior(){
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    historyChart.setData(new LineData(dataToday));
                    historyChart.notifyDataSetChanged();
                    historyChart.invalidate();
                } else if (tabLayout.getSelectedTabPosition() == 1){
                    historyChart.setData(new LineData(dataWeek));
                    historyChart.notifyDataSetChanged();
                    historyChart.invalidate();
                } else if (tabLayout.getSelectedTabPosition() == 2){
                    historyChart.setData(new LineData(dataMonth));
                    historyChart.notifyDataSetChanged();
                    historyChart.invalidate();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @AfterViews
    void dataManipulation(){
        YAxis leftAxis = historyChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(1000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Hello BLYAD!");

        //Firebase Variables
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mReference = database.getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Firebase UserID
        UserID = currentUser.getUid();

        //Firebase Database Reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReferecence = mFirebaseDatabase.getReference();
        super.onCreate(savedInstanceState);

        //Array to store retrieved values
        hourMeasurementsInDay = new ArrayList[24];
        for (int i = 0; i < 24; i++) {
            hourMeasurementsInDay[i] = new ArrayList<Integer>();
        }

        //Variables to store intermediate data, such as average day temperature
        dailyAverage = 0;

        //Array to store hourly averages
        hourlyAverages = new int [24];

        //Time conversions
        dayToMillis = TimeUnit.DAYS.toMillis(1);
        weekToMillis = TimeUnit.DAYS.toMillis(  7);
        monthToMillis = TimeUnit.DAYS.toMillis(30);

        //DataSet declarations for LineChart
        ArrayList<Entry> yAxexDay = new ArrayList<>();
        ArrayList<Entry> yAxesWeek = new ArrayList<>();
        ArrayList<Entry> yAxesMonth = new ArrayList<>();
        graphIndex = 0;
        dataToday = new LineDataSet(yAxexDay, "Hourly");
        dataWeek = new LineDataSet(yAxesWeek, "Daily");
        dataMonth = new LineDataSet(yAxesMonth, "Weekly");



        Long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        currentDayMillis = calendar.getTimeInMillis();
        currentDayReference = calendar.getTimeInMillis();


        mReference.child("Readings").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Read value is:" + dataSnapshot.getChildren().toString());
                getMonthlyAverages(currentDayMillis, dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public int getDailyAverage(long currentDayMillis, DataSnapshot dataSnapshot){
        long newDayMillis = currentDayMillis;
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            timestampMillis = Long.parseLong(snapshot.getKey());
            if(timestampMillis < newDayMillis && timestampMillis > (newDayMillis - dayToMillis)){
                Calendar sortCal = Calendar.getInstance();
                sortCal.setTimeInMillis(timestampMillis);
                int hour = sortCal.get(Calendar.HOUR_OF_DAY);
                hourMeasurementsInDay[hour].add(Integer.parseInt(snapshot.child("Temperature").getValue().toString()));
            }
        }
        int nonZeroHourlyAverages = 0;
        for (int i = 0; i < 24; i++){
            for (int j = 0; j < hourMeasurementsInDay[i].size(); j++){
                hourlyAverages[i] += hourMeasurementsInDay[i].get(j);
            }
            if (hourMeasurementsInDay[i].size() > 0) {
                hourlyAverages[i] = hourlyAverages[i] / hourMeasurementsInDay[i].size();
                if (newDayMillis == currentDayReference) {
                    dataToday.addEntry(new Entry(i, hourlyAverages[i]));
                }
                if (hourlyAverages[i] > 0) {
                    nonZeroHourlyAverages++;
                }
                dailyAverage += hourlyAverages[i];
            }
        }
        if(nonZeroHourlyAverages == 0){
            nonZeroHourlyAverages = 1;
        }
        dailyAverage = dailyAverage/nonZeroHourlyAverages;
        hourlyAverages = new int [24];
        hourMeasurementsInDay = new ArrayList[24];
        for (int i = 0; i < 24; i++) {
            hourMeasurementsInDay[i] = new ArrayList<Integer>();
        }
        return dailyAverage;
    }

    public void getMonthlyAverages(long currentDayMillis, DataSnapshot dataSnapshot){
        long newDayMillis = currentDayMillis;
        for (int i = 0; i < 30; i++){
            int newDailyAverage = getDailyAverage(newDayMillis, dataSnapshot);
            if (i < 7){
                dataWeek.addEntry(new Entry(i, newDailyAverage));
            }
            dataMonth.addEntry(new Entry(i, newDailyAverage));
            newDayMillis -=dayToMillis;
            dailyAverage = 0;
        }
    }

}
