package monash.fyp.attendanceapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.EddystoneUID;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Class representing the main page after authentication successful (login)
 */
public class Homepage extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference mainReference, databaseReference;
    private String studentIC;
    private StudentInfo student;
    private ClassInfo unit;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public static final String VIEW_HISTORY_KEY = "history_key";

    private ArrayList<Integer> rssi_A_list = new ArrayList<>();
    private ArrayList<Integer> rssi_B_list = new ArrayList<>();
    private ArrayList<Integer> rssi_C_list = new ArrayList<>();

    // reference id of the known reference points (beacons)
    private String referenceID_A = "0117C59B6024";
    private String referenceID_B = "0217C59B461D";
    private String referenceID_C = "0317C59B6024";

    // flag indicating the status (found/not found) of the reference points
    private boolean A_exist = false;
    private boolean B_exist = false;
    private boolean C_exist = false;

    // distance to reference points calculated
    private double dis_A = 0.0, dis_B = 0.0, dis_C = 0.0;

    // reference points ( coordinate of beacons in specified location )
    private double x1 = 0.0, x2 = 0.0, x3 = 0.0, y1 = 0.0, y2 = 0.0, y3 = 0.0;

    // classroom width and height -> area of specified location
    private double width, height;

    // login status (success/fail)
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mHandler = new Handler();
        TextView name = findViewById(R.id.valid_nameText);
        TextView id = findViewById(R.id.valid_IDText);

        // initialize database reference
        database = FirebaseDatabase.getInstance();
        mainReference = database.getReference();

        // get all the data required
        student = (StudentInfo)getIntent().getSerializableExtra(MainActivity.STUDENT_KEY);
        studentIC = student.getIdentification_number();

        // display student details on home page
        name.setText(student.getName());
        id.setText(student.getStudent_id());

        // Initializes a Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }


    /***
     * A method to start attendance taking process
     * @param view widget using this method
     */
    public void startScanning(View view){
        // check if location permission is allowed
        // prompt user to allow location permission if not yet allowed
        if (!requestLocationPermission()){
            return;
        }

        // check if user's time is set to automatic or not
        // prevent cheating
        if (! (android.provider.Settings.Global.getInt(getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) == 1 )) {
            Toast.makeText(getApplicationContext(), "Please enable automatic date and time in the settings", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if bluetooth is enabled
        // prompt user to enable bluetooth if not yet enabled
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        // start scanning for nearby ble signals
        else {
            Toast.makeText(getApplicationContext(), "Start scanning for beacons nearby", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Please stay still and don't close the app", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Collecting data..", Toast.LENGTH_LONG).show();
            scanLeDevice(true);
        }
    }


    /***
     * Main function for scanning nearby device for ble signals
     * @param enable true (start scan) or false (stop scan)
     */
    private void scanLeDevice(final boolean enable){
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {
            // Stops scanning after a pre-defined scan period (10 seconds).
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    Toast.makeText(getApplicationContext(), "Scan has completed", Toast.LENGTH_SHORT).show();
                    System.out.println("before "+ rssi_A_list);
                    preProcessing(rssi_A_list);
                    System.out.println("after" + rssi_A_list);
                    recordAttendance();
                }
            }, SCAN_PERIOD);

            bluetoothLeScanner.startScan(mLeScanCallback);

        } else {
            bluetoothLeScanner.stopScan(mLeScanCallback);
            Toast.makeText(getApplicationContext(), "Stop scanning", Toast.LENGTH_LONG).show();
        }
    }


    /***
     * Device scan callback.
     */
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            final List<ADStructure> structures = ADPayloadParser.getInstance().parse(Objects.requireNonNull(result.getScanRecord()).getBytes());
            // run background operations on worker thread and update the result on main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (ADStructure structure:structures){
                        // scans for Eddystone beacons only
                        if (structure instanceof EddystoneUID){
                            EddystoneUID es = (EddystoneUID)structure;
                            String referenceID = es.getInstanceIdAsString();
                            Integer RSSI = result.getRssi();

                            // stores all the RSSI values received from the three beacons
                            if (referenceID.equals(referenceID_A)){
                                A_exist = true;
                                rssi_A_list.add(RSSI);
                            }
                            if (referenceID.equals(referenceID_B)){
                                B_exist = true;
                                rssi_B_list.add(RSSI);
                            }
                            if (referenceID.equals(referenceID_C)){
                                C_exist = true;
                                rssi_C_list.add(RSSI);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    /***
     * Main function to perform check and record student's attendance in database
     */
    public void recordAttendance(){
        final ArrayList<String> class_list = student.getClassList();

        // update database reference
        database = FirebaseDatabase.getInstance();
        mainReference = database.getReference();
        databaseReference = mainReference.child("class_info");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // for every class the student has enrolled
                for (int i = 0; i < class_list.size(); i++){
                    final String unitCode = class_list.get(i);

                    // navigate down the tree to respective child (unit code)
                    if (!unitCode.isEmpty()) {
                        unit = dataSnapshot.child(unitCode).getValue(ClassInfo.class);

                        // check what class student is attending
                        assert unit != null;
                        HashMap<String, HashMap<String, Integer>> beacons = unit.getBeacons();
                        if (A_exist && beacons.containsKey(referenceID_A) && B_exist && beacons.containsKey(referenceID_B) && C_exist && beacons.containsKey(referenceID_C)){
                            width = Double.valueOf(unit.getRoom_width()); height = Double.valueOf(unit.getRoom_height());
                            x1 = Double.valueOf(beacons.get(referenceID_A).get("x")); y1 = Double.valueOf(beacons.get(referenceID_A).get("y"));
                            x2 = Double.valueOf(beacons.get(referenceID_B).get("x")); y2 = Double.valueOf(beacons.get(referenceID_B).get("y"));
                            x3 = Double.valueOf(beacons.get(referenceID_C).get("x")); y3 = Double.valueOf(beacons.get(referenceID_C).get("y"));

                            // get current time
                            String currentTime = new SimpleDateFormat("dd-MM-yyyy, HH:mm").format(new Date());

                            // check if student is inside the class
                            if (withinRange()) {
                                for (String class_session : unit.getSessions().keySet()) {
                                    // if there is a class session going on
                                    if (compareTimestamp(currentTime, class_session)) {
                                        // record student's attendance on database and display success message
                                        databaseReference.child(unitCode).child("sessions").child(class_session).child(studentIC).setValue(true);
                                        flag = true;
                                        Toast.makeText(getApplicationContext(), "Successfully taken attendance!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                break;
                            } } } }

                // any of the check fails (ie. out of range from any beacons, not inside classroom, no class session available on current time)
                // displays fail message
                if (!flag){
                    Toast.makeText(getApplicationContext(), "Failed to take attendance!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /***
     * A method to check whether current time is within the session
     * @param curr_time current timestamp (eg. 28-05-2020, 12:12)
     * @param class_session session (eg. 28-05-2020, 13:00 - 14:00)
     * @return true/ false
     */
    private boolean compareTimestamp(String curr_time, String class_session) {

        String pattern = "dd-MM-yyyy";
        String timePat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        SimpleDateFormat tdf = new SimpleDateFormat(timePat);

        try {
            // get the date for comparison
            Date date1 = sdf.parse(curr_time.substring(0, 10));
            Date date2 = sdf.parse(class_session.substring(0, 10));

            // get the time for comparison
            Date time1 = tdf.parse(class_session.substring(12, 17));
            Date time2 = tdf.parse(class_session.substring(20, 25));
            Date currTime = tdf.parse(curr_time.substring(12));

            if(date1.equals(date2) &&  currTime.after(time1) && currTime.before(time2))  {
                return true;
            }
            else {
                return false;
            }

        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }


    /***
     * Callback for the result from requesting permissions
     * @param requestCode request code passed in
     * @param permissions requested permissions. never null
     * @param grantResults grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * Method to check if location permission is allowed
     * Request user to allow if not yet allowed
     * @return true/false
     */
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public boolean requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            return true;
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
            return false;
        }
    }

    /***
     * A method to check if user is within the specified area
     * @return true/false
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public boolean withinRange(){
        // calculate the estimated distance from user to each beacons
        if (rssi_A_list.size() > 0){
            Integer average_A = getAverage(rssi_A_list);
            dis_A = getDistance(average_A);

        }
        if (rssi_B_list.size() > 0){
            Integer average_B = getAverage(rssi_B_list);
            dis_B = getDistance(average_B);

        }
        if (rssi_C_list.size() > 0){
            Integer average_C = getAverage(rssi_C_list);
            dis_C = getDistance(average_C);

        }

        // check if user is within the specified area
        if (dis_A > 0 && dis_B > 0 && dis_C > 0 ) {
            return valid_xy(dis_A, dis_B, dis_C);
        }
        // return false if any one of the beacon is out of range
        else {
            return false;
        }
    }


    /***
     * A method to get the average rssi value
     * @param rssi_list an array list containing all the rssi value
     * @return average value
     */
    public Integer getAverage(ArrayList<Integer> rssi_list){
        Integer sum = 0;
        if (rssi_list.size() > 0) {
            for (Integer rssi : rssi_list) {
                sum += rssi;
            }
            return sum / rssi_list.size();
        }
        else{
            return 0;
        }
    }


    /***
     * A method to get the variance of  rssi value
     * @param rssi_list an array list containing all the rssi value
     * @return variance value
     */
    public double getVariance(ArrayList<Integer> rssi_list){
        double sumDiffSquared = 0.0;
        if (rssi_list.size() > 0) {
            double avg = getAverage(rssi_list);
            for (Integer rssi : rssi_list) {
                double diff = rssi - avg;
                diff *= diff;
                sumDiffSquared += diff;
            }
            return sumDiffSquared / (rssi_list.size());
        }
        else{
            return 0.0;
        }
    }

    /***
     * pre-processing
     */
    public void preProcessing(ArrayList<Integer> rssi_list){
        if (rssi_list.size() > 0){
            Integer mean = getAverage(rssi_list);
            double std = Math.sqrt(getVariance(rssi_list));
            Integer front = (int) (mean - std);
            Integer back = (int) (mean + std);
            int i = 0;
            while (i < rssi_list.size()){
                if (rssi_list.get(i) < front || rssi_list.get(i) > back) {
                    rssi_list.remove(i);
                }
                else{
                    i++;
                }
            }
        }
    }

    /***
     * A method to estimate the distance to beacon using rssi value using rssi model
     * @param rssi rssi value
     * @return distance to beacon
     */
    public double getDistance(Integer rssi){
        int A = -88;
        double n = 2.3;
        return Math.pow(10, ((rssi - A) / (-10 * n)) );
    }


    /***
     * Calculate user's coordinate using Trilateration and check if user is within specified area
     * @param dis_A distance to first beacon
     * @param dis_B distance to second beacon
     * @param dis_C distance to third beacon
     * @return true/false
     */
    @SuppressLint("DefaultLocale")
    public boolean valid_xy(double dis_A, double dis_B, double dis_C){

        // trilateration formula
        double value1 = ( Math.pow(dis_A, 2) - Math.pow(dis_B, 2) ) - ( Math.pow(x1, 2) - Math.pow(x2, 2) ) - ( Math.pow(y1, 2) - Math.pow(y2, 2) );
        double value2 = ( Math.pow(dis_A, 2) - Math.pow(dis_C, 2) ) - ( Math.pow(x1, 2) - Math.pow(x3, 2) ) - ( Math.pow(y1, 2) - Math.pow(y3, 2) );
        double denominator = ( 4 * (x2 - x1) * (y3 - y1) ) - ( 4 * (x3 - x1) * (y2 - y1) );

        // user's x and y coordinate estimated
        double x = ( (value1 * 2 * (y3-y1)) - (value2 * 2 * (y2-y1)) ) / denominator;
        double y = ( (value2 * 2 * (x2-x1)) - (value1 * 2 * (x3-x1)) ) / denominator;

        // check if user is within specified area
        if (x >= 0 && x <= width && y >= 0 && y <= height ){
            return true;
        }
        return false;
    }


    /**
     * A method to retrieve the attendance records for the student and start a new activity to display them
     * @param view widget using this method
     */
    public void viewHistory(View view) {
        final ArrayList<String> class_list = student.getClassList();
        final ArrayList<RecyclerItem> data_array = new ArrayList<>();
        studentIC = student.getIdentification_number();

        // update database reference
        database = FirebaseDatabase.getInstance();
        mainReference = database.getReference();
        databaseReference = mainReference.child("class_info");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // for every class the student has enrolled
                for (int i = 0; i < class_list.size(); i++){
                    final String unitCode = class_list.get(i);

                    // navigate down the tree to respective child (unit code)
                    if (!unitCode.isEmpty()) {
                        unit = dataSnapshot.child(unitCode).getValue(ClassInfo.class);

                        // retrieve and store all previous attendance record
                        assert unit != null;
                        for (String class_session : unit.getSessions().keySet()){
                            HashMap<String, Boolean> stud_list = unit.getSessions().get(class_session);
                            assert stud_list != null;
                            if (stud_list.containsKey(studentIC) && stud_list.get(studentIC)) {
                                RecyclerItem newRecyclerItem = new RecyclerItem(unitCode, unit.getUnit_name(), class_session);
                                data_array.add(newRecyclerItem);

                            } } } }

                // pass the records to view history activity to be displayed
                Intent intent = new Intent(Homepage.this, ViewHistory.class);
                intent.putExtra(VIEW_HISTORY_KEY, data_array);
                startActivity(intent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
