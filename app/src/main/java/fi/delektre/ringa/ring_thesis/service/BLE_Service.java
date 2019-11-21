package fi.delektre.ringa.ring_thesis.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.polidea.rxandroidble2.utils.ConnectionSharingAdapter;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import fi.delektre.ringa.ring_thesis.constants.AppConst;
import fi.delektre.ringa.ring_thesis.util.RetryWithDelay;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.LineDataCollection;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static fi.delektre.ringa.ring_thesis.util.ChartUtils.removeOutdatedEntries;


@EService
public class BLE_Service extends Service {

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference mReferecence;
    private FirebaseDatabase mFirebaseDatabase;


    private String UserID;

    //BLE related vars
    private String TAG = "RINGA";
    private RxBleClient rxBleClient = null;
    private RxBleDevice rxBleDevice = null;
    private Disposable scanSubscription = null;
    private BLE_ConnectionCheck BLE_ConnetionCheck = null;
    private Observable<RxBleConnection> connectionObservable;
    private PublishSubject<Void> disconnectTriggerSubject = PublishSubject.create();

    //Cloud calculations
    private ArrayList<Integer> tempArrayList;

    //Chart-related vars
    private int graphIndex;
    public LineDataSet lineDataSetTemperature1;
    public LineDataSet lineDataSetTemperature2;
    public LineDataSet lineDataSetBloodPressure;
    public LineDataSet lineDataSetPulse;
    public LineDataSet lineDataSetFluidBalance;
    public ArrayList<ILineDataSet> lineDataSetsTemperatures;

    //LineDataCollection object
    public LineDataCollection parseData;

    //System time snapshot
    public Long currentTimeMillis;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        BLE_ConnetionCheck = new BLE_ConnectionCheck(getApplicationContext());
        registerReceiver(BLE_ConnetionCheck, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

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


        tempArrayList = new ArrayList<>();
        super.onCreate();

        //DataSets for the LineCharts to minimize calculations in Main Activity

        ArrayList<String> xAxes = new ArrayList<>();

        ArrayList<Entry> yAxesTemp1 = new ArrayList<>();
        ArrayList<Entry> yAxesTemp2 = new ArrayList<>();
        ArrayList<Entry> yAxesBloodPressure = new ArrayList<>();

        parseData = new LineDataCollection();
        lineDataSetsTemperatures = new ArrayList<>();

        lineDataSetTemperature1 = new LineDataSet(yAxesTemp1, "Temp 1");
        lineDataSetTemperature1.setDrawCircles(false);
        lineDataSetTemperature1.setColor(Color.BLUE);

        lineDataSetTemperature2 = new LineDataSet(yAxesTemp2, "Temp 2");
        lineDataSetTemperature2.setDrawCircles(false);
        lineDataSetTemperature2.setColor(Color.RED);
        lineDataSetsTemperatures.add(lineDataSetTemperature1);
        lineDataSetsTemperatures.add(lineDataSetTemperature2);
        graphIndex = 0;
        findDevice();
        fetchDataTimer();


        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();
        int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public void fetchData() {
        if (connectionObservable != null) {
            connectionObservable
                    .flatMapSingle(rxBleConnection -> rxBleConnection.readCharacteristic(AppConst.UUID_DATA_CHARACTERISTIC))
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(new RetryWithDelay(3, 20000  ))
                    .subscribe(bytes -> {
                                showData(bytes);
                            },
                            this::onReadFailure);
        }

    }

    private void onReadFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Read error: " + throwable,
                Toast.LENGTH_LONG).show();


    }

    void fetchDataTimer(){
        Observable disposable = Observable.interval(300, TimeUnit.MILLISECONDS);
        disposable.subscribeOn(Schedulers.from(Executors.newFixedThreadPool(10))).subscribe(t -> {
           fetchData();
        });
    }

    void showData(byte[] bytes) {


        //Takes every second byte in the array
        //And if it is less then 0, shift it to a positive value
        //Add all those values to the array
        int shiftedBytes[] = new int[8];
        for (int i = 0; i < 14; i++){
            if (i%2 == 0){
                shiftedBytes[i/2] = bytes[i];
                if (shiftedBytes[i/2] < 0){
                    shiftedBytes[i/2] = shiftedBytes[i/2] + 255;
                }
            }
        }
        int zero1 = (int) bytes[0];
        int redS = (int) bytes[1] << 8| shiftedBytes[1];
        int greenS = (int) bytes[3] << 8| shiftedBytes[2];
        int blueS = (int) bytes[5] << 8| shiftedBytes[3];
        int nirS = (int) bytes[7] << 8| shiftedBytes[4];
        int yellowS = (int) bytes[9] << 8| shiftedBytes[5];
        int tempByte12 = bytes[12];
        if (tempByte12 < 0){
            tempByte12 = tempByte12 + 255;
        }
        int temp1S = (int) bytes[11] << 8| shiftedBytes[6];
        int temp2S = (int) bytes[13] << 8| shiftedBytes[7];
        int steps = bytes[15] | (bytes[16] << 8) | (bytes[17] << 16) | (bytes[18] << 24);
        int zero2 = bytes[19];

        int byte11 = bytes[11] << 8;
        int byte12 = tempByte12;
        int byte12shift = byte12 << 8;
        int byte13 = bytes[13];
        int byte14 = bytes[14];
        int byte14shift = bytes[14] << 8;

        float temperature1 = temp1S / 10f;
        graphIndex++;
        lineDataSetTemperature1.addEntry(new Entry(graphIndex, temperature1));
        lineDataSetTemperature2.addEntry(new Entry(graphIndex, temperature1));
        lineDataSetsTemperatures.set(0, lineDataSetTemperature1);
        lineDataSetsTemperatures.set(1, lineDataSetTemperature2);

        removeOutdatedEntries(lineDataSetTemperature1);
        removeOutdatedEntries(lineDataSetTemperature2);

        LineData tempData = new LineData(lineDataSetsTemperatures);
        parseData.setTemp(tempData);

        RxBus.publish(parseData);

        if (tempArrayList.size() < 60) {
            tempArrayList.add((int) temperature1);
        } else {
            int avgTemp = 0;
            for (int temp : tempArrayList){
                avgTemp += temp;
            }
            avgTemp = avgTemp / 60;
            currentTimeMillis = System.currentTimeMillis();

            mReferecence.child("Readings").child(UserID).child(String.valueOf(currentTimeMillis))
                    .child("Temperature").setValue(avgTemp);
            tempArrayList.clear();
            tempArrayList = new ArrayList<>();
        }
        long timeSnapshot = System.currentTimeMillis();
        mReferecence.child("LED_Test").child(UserID)
                .child(String.valueOf(timeSnapshot)).child("RED_S").setValue(redS);
        mReferecence.child("LED_Test").child(UserID)
                .child(String.valueOf(timeSnapshot)).child("GREEN_S").setValue(greenS);
        mReferecence.child("LED_Test").child(UserID)
                .child(String.valueOf(timeSnapshot)).child("BLUE_S").setValue(blueS);
        mReferecence.child("LED_Test").child(UserID)
                .child(String.valueOf(timeSnapshot)).child("NIR_S").setValue(nirS);
        mReferecence.child("LED_Test").child(UserID).
                child(String.valueOf(timeSnapshot)).child("YELLOW_S").setValue(yellowS);

        Log.d("DATARECIEVED", "Red: " + redS + " Green: " + greenS + " Blue: " + blueS + " Nir: "  + nirS + " Yellow: " + yellowS + " Steps:" + steps + " Temp1:" + temp1S + " Temp2:" + temp2S);
        Log.d("TEMP1_LOG", "Byte 11: " + byte11 +  "|Byte 12:" + byte12 + "|SHIFTED Byte12: " + byte12shift +"|TOGETHER:" + temp1S);
        Log.d("TEMP2_LOG", "Byte 13: " + byte13 + "|Byte 14:" + byte14 + "|SHIFTED Byte14: " + byte14shift);
        Log.d("READ BYTES", "Read: " + bytes.length + " bytes " + CRC8(bytes));
    }

    protected void findDevice() {
        if (rxBleClient == null) {
            Log.e(TAG, "Missing BLE client, creating");
            rxBleClient = RxBleClient.create(this);
        }

        scanSubscription = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        //.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        //.setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                        .build(),
                new ScanFilter.Builder()
                        .setDeviceName("RINGA")
                        .build()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(this::clearSubscription)
                .take(1)
                .subscribe(this::onScanSuccess, this::onScanFailure);
    }

    protected void clearSubscription() {
        if (scanSubscription != null) {
            scanSubscription = null;
        }
    }
    @UiThread
    public void onScanSuccess(ScanResult scanResult) {
        Log.e(TAG, "Found: " + scanResult);
        rxBleDevice = scanResult.getBleDevice();
        String macAddr = scanResult.getBleDevice().getMacAddress();

        connectionObservable = prepareConnectionObservable();
        if (scanSubscription != null)
            scanSubscription.dispose();
    }

    public static byte CRC8(byte[] bytes) {
        byte generator = (byte) 0x07;
        byte crc = 0;
        for (byte currByte : bytes) {
            crc ^= currByte;

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    crc = (byte) ((crc << 1) ^ generator);
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc;
    }

    private Observable<RxBleConnection> prepareConnectionObservable() {
        return rxBleDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .doOnDispose(this::clearSubscription)
                .compose(new ConnectionSharingAdapter());
    }

    private void onScanFailure(Throwable throwable) {

        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
        connectionObservable.unsubscribeOn(AndroidSchedulers.mainThread());
    }

    private void handleBleScanException(BleScanException bleScanException) {
        final String text;

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                text = "Bluetooth is not available";

                break;
            case BleScanException.BLUETOOTH_DISABLED:
                text = "Enable bluetooth and try again";

                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                text = "On Android 6.0 location permission is required. Implement Runtime Permissions";

                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                text = "Location services needs to be enabled on Android 6.0";

                break;
            case BleScanException.SCAN_FAILED_ALREADY_STARTED:
                text = "Scan with the same filters is already started";

                break;
            case BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                text = "Failed to register application for bluetooth scan";

                break;
            case BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED:
                text = "Scan with specified parameters is not supported";

                break;
            case BleScanException.SCAN_FAILED_INTERNAL_ERROR:
                text = "Scan failed due to internal error";

                break;
            case BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES:
                text = "Scan cannot start due to limited hardware resources";

                break;
            case BleScanException.UNKNOWN_ERROR_CODE:
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                text = "Unable to start scanning";
                break;
        }
        Log.w("EXCEPTION", text, bleScanException);}
}
