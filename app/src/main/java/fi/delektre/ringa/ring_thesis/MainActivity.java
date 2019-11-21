package fi.delektre.ringa.ring_thesis;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Environment;
import android.os.Debug;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;


//import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;


import java.util.Timer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
//import com.polidea.rxandroidble.RxBleCustomOperation;
import com.polidea.rxandroidble2.RxBleDevice;
//import com.polidea.rxandroidble.exceptions.BleGattCannotStartException;
//import com.polidea.rxandroidble.exceptions.BleGattOperationType;
//import com.polidea.rxandroidble.internal.connection.RxBleGattCallback;
//import com.polidea.rxandroidble.internal.util.ByteAssociation;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;



import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.service.BLE_ConnectionCheck;
import fi.delektre.ringa.ring_thesis.service.BLE_Service_;
import fi.delektre.ringa.ring_thesis.util.BasicActivity;
import fi.delektre.ringa.ring_thesis.util.LineDataCollection;
import fi.delektre.ringa.ring_thesis.util.SelectorOption;
import fi.delektre.ringa.ring_thesis.adapters.SelectorRecyclerViewAdapter;
import im.delight.android.location.SimpleLocation;
//import rx.Emitter;
//import rx.Scheduler;
import io.reactivex.disposables.Disposable;
//import rx.functions.Func1;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


@EActivity(R.layout.layout_main_activity)
public class MainActivity extends BasicActivity {
    private String TAG = "RINGA";

    //LineChart Definition
    public LineDataSet lineDataSetTemperature1;
    public LineDataSet lineDataSetTemperature2;
    public LineDataSet lineDataSetBloodPressure;
    public LineDataSet lineDataSetPulse;
    public LineDataSet lineDataSetFluidBalance;
    public ArrayList<ILineDataSet> lineDataSetsTemperatures;
    String[] xaxes;
    //Coursor for charts
    protected int graphIndex = 0;


    public static final String AUTHORITY = "fi.delektre.ringa.ring_thesis.provider";
    public static final String ACCOUNT_TYPE = "delektre.fi";
    public static final String ACCOUNT = "dummyaccount";
    public static final Long SECONDS_PER_MINUTE = 60L;
    public static final Long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final Long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;



    private StorageReference defaultProfilePic;
    private StorageReference profilePic;


    @ViewById(R.id.cardview_main_1)
    protected CardView cardView;

    @ViewById(R.id.main_coordinator_layout)
    protected CoordinatorLayout coordinatorLayout;

    @ViewById(R.id.mainScreen)
    protected NestedScrollView nestedScrollView;

    @ViewById(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    @ViewById(R.id.rootLayout)
    protected View rootLayout;

    @ViewById(R.id.toolbar_profile)
    protected ImageView profilePicture;

    @ViewById(R.id.main_appbarlayout)
    protected AppBarLayout appBarLayout;

    @ViewById(R.id.lineChart)
    LineChart lineChart;




    @Click(R.id.fabTest)
    void buttonTestData() {
        //graphIndex = adapter.graphIndex;
        graphIndex++;
        Log.d("TEST", "TEST INDEX:" + graphIndex);
        //adapter.graphIndex = graphIndex;
        int min = 0;
        int max = 30;
        float temperature1 = (float) Math.random() * ((max - min) + 1);
        float temperature2 = (float) Math.random() * ((max - min) + 1);
        adapter.lineDataSetTemperature1.addEntry(new Entry(graphIndex, temperature1));
        Log.d("TEST", "TEST TEMP 1:" + temperature1);
        Log.d("TEST", "TEST TEMP 2:" + temperature2);
        adapter.lineDataSetTemperature2.addEntry(new Entry(graphIndex, temperature2));
        float test1 = (float) Math.random() * ((max - min) + 1);
        float test2 = (float) Math.random() * ((max - min) + 1);
        float test3 = (float) Math.random() * ((max - min) + 1);
        adapter.lineDataSetBloodPressure.addEntry(new Entry(graphIndex, test1));
        adapter.lineDataSetPulse.addEntry(new Entry(graphIndex, test2));
        adapter.lineDataSetFluidBalance.addEntry(new Entry(graphIndex, test3));
        adapter.lineDataSetsTemperatures.set(0, adapter.lineDataSetTemperature1);
        adapter.lineDataSetsTemperatures.set(1, adapter.lineDataSetTemperature2);
        lineChart.setData(new LineData(adapter.lineDataSetsTemperatures));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @ViewById
    RecyclerView recycler_view_graph_options;

    @Bean
    SelectorRecyclerViewAdapter adapter;

    @ViewById
    CardView cardview_main_recyclerholder;

    @AfterViews
    void bindAdapter(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .HORIZONTAL, false);
        recycler_view_graph_options.setLayoutManager(layoutManager);
        adapter.items.add(new SelectorOption(R.drawable.ic_body_temp, "Body Temperature"));
        adapter.items.add(new SelectorOption(R.drawable.ic_water_balance, "Water Balance"));
        adapter.items.add(new SelectorOption(R.drawable.ic_pulse, "Pulse"));
        adapter.items.add(new SelectorOption(R.drawable.ic_blood_pressure, "Blood Pressure"));
        adapter.lineChart = lineChart;
        adapter.initGraphsDataSets();
        adapter.buttonPosition = 0;
        recycler_view_graph_options.setAdapter(adapter);
    }

    @AfterViews
    void viewsManipulation(){

        super.onCreateDrawer();
        //Firebase instance declaration
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mReference = database.getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String UserID = currentUser.getUid();

        //Chart View Setup
        lineChart.setData(new LineData(lineDataSetsTemperatures));
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1);

        //lineChart.setVisibleXRangeMinimum(10);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(30);


        lineChart.setVisibleYRangeMinimum(0, YAxis.AxisDependency.LEFT);
        lineChart.setVisibleYRangeMaximum(30, YAxis.AxisDependency.LEFT);
        lineChart.invalidate();

        //Changing CoordinatorLayout height to match new offset
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ViewGroup.LayoutParams params = coordinatorLayout.getLayoutParams();
        int dp = 50;
        float px;
        Resources r = getResources();
        px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

        params.height = size.y + (int)px;
        coordinatorLayout.setLayoutParams(params);

        //Getting username from the Cloud/instance of Firebase Auth
        mReference.child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mUserName = getString(R.string.main_bottom_sheet_content) + " " + dataSnapshot.child("Name").getValue();
                Toast toast = Toast.makeText(getApplicationContext(), mUserName, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        defaultProfilePic = storageRef.child("default-files/default_ph.jpg");
        //Gets the URI of the stored picture.
        //Then, on success, uses Picasso to send it to profilePicture ImageView,
        //resizes it to square and rounds the corner to make a circle.
        defaultProfilePic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).
                        resize(toolbar.getHeight()-5, toolbar.getHeight()-5)
                        .centerCrop()
                        .into(profilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        profilePicture.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError(Exception e) {

                    }
                });
            }


        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        BLE_Service_.intent(getApplication()).start();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Log.d(TAG, "onCreate()");
        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Chart Stuff

        ArrayList<String> xAxes = new ArrayList<>();

        ArrayList<Entry> yAxesTemp1 = new ArrayList<>();
        ArrayList<Entry> yAxesTemp2 = new ArrayList<>();
        ArrayList<Entry> yAxesBloodPressure = new ArrayList<>();

        lineDataSetsTemperatures = new ArrayList<>();

        lineDataSetTemperature1 = new LineDataSet(yAxesTemp1, "Temp 1");
        lineDataSetTemperature1.setDrawCircles(false);
        lineDataSetTemperature1.setColor(Color.BLUE);

        lineDataSetTemperature2 = new LineDataSet(yAxesTemp2, "Temp 2");
        lineDataSetTemperature2.setDrawCircles(false);
        lineDataSetTemperature2.setColor(Color.RED);

        lineDataSetsTemperatures.add(lineDataSetTemperature1);
        lineDataSetsTemperatures.add(lineDataSetTemperature2);

        fetchData();
        askPermissions();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause() called");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bind_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private final int REQUEST_ENABLE_BT = 578;

    private static final int PERMISSION_REQUEST_ALL = 1000;

    private void askPermissions() {
        Log.d(TAG, "askPermissions()");
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BLUETOOTH,
                },
                PERMISSION_REQUEST_ALL);
    }

    public void fetchData() {
        RxBus.subscribe((parseData) -> {
            if (parseData instanceof LineDataCollection){
                LineDataCollection chartData = (LineDataCollection) parseData;
                if (adapter.buttonPosition == 0){
                    adapter.tempData = (chartData.getTemp());
                    lineChart.setData(chartData.getTemp());
                }
            }
            lineChart.notifyDataSetChanged(); // let the chart know it's data changed
            lineChart.invalidate();
        });
    }

    @OnActivityResult(REQUEST_ENABLE_BT)
    void onEnableBTResult(int resultCode) {
        if (resultCode != RESULT_OK) {
            finish();
        }
    }

}
