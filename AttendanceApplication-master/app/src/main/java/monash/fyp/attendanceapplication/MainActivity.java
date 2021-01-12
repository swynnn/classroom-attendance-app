package monash.fyp.attendanceapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Application starts with Main activity
 * Contains functionality for login button
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference mainReference;
    private DatabaseReference databaseReference;
    private StudentInfo student;

    public static final String STUDENT_KEY = "student_key";
    private SignetClientHelper signetClientHelper;

    // application certificate
    private String appcert = "MIIDrjCCAzWgAwIBAgIIXRPbRBE/lcYwCgYIKoZIzj0EAwMwcTEtMCsGA1UEAwwk\n" +
            "TUlNT1MgRUNDIFNJR05FVCBBUFBMSUNBVElPTiBDQSAtIEcyMRUwEwYDVQQKDAxN\n" +
            "SU1PUyBCRVJIQUQxHDAaBgNVBAgME1dJTEFZQUggUEVSU0VLVVRVQU4xCzAJBgNV\n" +
            "BAYTAk1ZMB4XDTIwMDQxNDA5Mjc0OVoXDTQ5MDMxMjA5MjIxMVowKzEpMCcGA1UE\n" +
            "AwwgbW9uYXNoLmZ5cC5hdHRlbmRhbmNlYXBwbGljYXRpb24wWTATBgcqhkjOPQIB\n" +
            "BggqhkjOPQMBBwNCAATUevDOs+jxXQj7Z+rcxsrSBMmmid+trup+N+k0IqeRm8P0\n" +
            "JtjOtZPw4QhruMMmfvVcJxJ+2Q8BBW17j0wgfs99o4IB+zCCAfcwDAYDVR0TAQH/\n" +
            "BAIwADAfBgNVHSMEGDAWgBQ09BV8EDXiAgiYH0bJPFEdoaBxYDBRBggrBgEFBQcB\n" +
            "AQRFMEMwQQYIKwYBBQUHMAGGNWh0dHA6Ly9taW1vc2NhLm1pbW9zLm15L21pbW9z\n" +
            "Y2EvcHVibGljd2ViL3N0YXR1cy9vY3NwMBMGA1UdJQQMMAoGCCsGAQUFBwMCMIIB\n" +
            "PQYDVR0fBIIBNDCCATAwggEsoIGyoIGvhoGsaHR0cDovL21pbW9zY2EubWltb3Mu\n" +
            "bXkvbWltb3NjYS9wdWJsaWN3ZWIvd2ViZGlzdC9jZXJ0ZGlzdD9jbWQ9Y3JsJmlz\n" +
            "c3Vlcj1DTj1NSU1PUyUyMEVDQyUyMFNJR05FVCUyMEFQUExJQ0FUSU9OJTIwQ0El\n" +
            "MjAtJTIwRzIsTz1NSU1PUyUyMEJFUkhBRCxTVD1XSUxBWUFIJTIwUEVSU0VLVVRV\n" +
            "QU4sQz1NWaJ1pHMwcTEtMCsGA1UEAwwkTUlNT1MgRUNDIFNJR05FVCBBUFBMSUNB\n" +
            "VElPTiBDQSAtIEcyMRUwEwYDVQQKDAxNSU1PUyBCRVJIQUQxHDAaBgNVBAgME1dJ\n" +
            "TEFZQUggUEVSU0VLVVRVQU4xCzAJBgNVBAYTAk1ZMB0GA1UdDgQWBBQTCVJYcZPF\n" +
            "jKud8PquO3mNvUIVQTAKBggqhkjOPQQDAwNnADBkAjBCvQg49l1dkU3hORqAWNRV\n" +
            "ydP7fOuF0hBU5AyiGxCbKzGQ9xhSvyXghetIx5qNvUICMHcYgl3cWSSer1s80kpO\n" +
            "iTBzll8ZMU2Hh2uOf11buhSaDNin1hGxUz1lN9bm+MUp1w==                  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        database = FirebaseDatabase.getInstance();
        mainReference = database.getReference();

        signetClientHelper = new SignetClientHelper(this);

        // debug purposes
        String versionName = BuildConfig.VERSION_NAME;
        TextView tv1 = findViewById(R.id.tv1);

        tv1.setText("Version: " + versionName);
    }


    /**
     * Check if user is a valid student in database
     * navigate to new page if student is valid
     * @param studentIC user's identification number
     */
    public void loginWithIC(String studentIC) {
        // update database reference
        databaseReference = mainReference.child("student_info");
        final String loginID = studentIC;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if student records exist in database
                if (!loginID.isEmpty() && dataSnapshot.child(loginID).exists()) {
                    student = dataSnapshot.child(loginID).getValue(StudentInfo.class);
                    // pass the student's details to next activity and start the next activity (home page)
                    Intent intent = new Intent(MainActivity.this, Homepage.class);
                    intent.putExtra(STUDENT_KEY, student);
                    startActivity(intent);

                } else {
                    // cant find user in firebase, display fail message to user
                    CharSequence text = "User does not exist!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /***
     * Method to initiate request to MyDigital ID
     * @param view widget using this method
     */
    public void loginWithMyDigitalID(View view) {
        // check if MyDigital ID is installed in user's phone, redirect to MyDigital ID if user have not downloaded yet
        if (!signetClientHelper.isSignetInstalled()){
            try{
                Toast.makeText(getApplicationContext(), "Please install MyDigital ID", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=my.mimos.signetclient")));
            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else {
            //request submit cert
            signetClientHelper.requestSubmitCert(appcert, SignetClientHelper.REQUEST_SUBMITCERT_CODE);
        }
    }


    /***
     * Result callback, called when result is available
     * @param requestCode request code passed in
     * @param resultCode result code
     * @param data intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // if the result callback if from request submit certificate request
        if (requestCode == SignetClientHelper.REQUEST_SUBMITCERT_CODE){
            // request successful
            if (resultCode == Activity.RESULT_OK) {
                // pass data received from myDigtial ID application to myDigital ID server
                String received_data = data.getStringExtra("data");
                server_request_cert submit_cert = new server_request_cert(received_data);
                // execute submit cert
                submit_cert.execute();

            }
            // request failed
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
            }

        }

        // if the result callback if from execute certificate request
        if (requestCode == SignetClientHelper.EXECUTE_SUBMITCERT_CODE){
            // request successful
            if (resultCode == Activity.RESULT_OK) {
                // pass data received from myDigtial ID application to myDigital ID server
                String received_result = data.getStringExtra("data");
                execute_submit_cert submit_cert = new execute_submit_cert(received_result);
                // execute submit cert
                submit_cert.execute();

            }
            // request failed
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Execute cancelled", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /***
     * Extract token from message
     * @param fullText message
     * @return extracted token
     */
    public String retrieveToken(String fullText){
        StringTokenizer sT = new StringTokenizer(fullText, ":");
        String version = sT.nextToken(); String status = sT.nextToken();
        String req = sT.nextToken(); String payload = sT.nextToken();
        int lastCharIndex = payload.length()-6;
        String token = payload.substring(1, lastCharIndex);
        return token;
    }


    /***
     * Extract encoded string from message
     * @param fullText message
     * @return extracted encoded string
     */
    public String getEncodedString(String fullText){
        String encoded = "";
        try {
            StringTokenizer sT = new StringTokenizer(fullText, ":");
            String version = sT.nextToken(); String status = sT.nextToken(); String payload = sT.nextToken();
            String x509 = sT.nextToken(); String x509_string = sT.nextToken();
            int lastCharIndex = x509_string.length()-2;
            encoded = x509_string.substring(1, lastCharIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }


    /***
     * Decode message received and extract user's identification number
     * @param encoded_string message received
     * @return user's identification number
     */
    public String extractSerialNumber(String encoded_string){
        String result = "";
        byte[] encodedCert = android.util.Base64.decode(encoded_string, android.util.Base64.DEFAULT);
        try {
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(encodedCert));
            String subjectDN = cert.getSubjectDN().toString();
            StringTokenizer sT = new StringTokenizer(subjectDN, ",");
            String text = sT.nextToken();
            result = sT.nextToken().substring(14);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /***
     * pass messages using server
     */
    private class server_request_cert extends AsyncTask<String, Integer, String> {
        String token;
        private server_request_cert(String retrievedToken){
            token = retrievedToken;
        }
        @Override
        protected String doInBackground(String... args){
            String serverResponse;

            // socket host and port
            SocketConnection login = new SocketConnection("208.188.87.34.bc.googleusercontent.com", 47858);
            serverResponse = login.request(token);

            login.closeConnection();
            return serverResponse;

        }

        protected void onPostExecute(String result){
            // execute submit cert
            signetClientHelper.executeSubmitCert(retrieveToken(result), appcert, SignetClientHelper.EXECUTE_SUBMITCERT_CODE);
        }
    }


    /***
     * pass messages using server
     */
    private class execute_submit_cert extends AsyncTask<String, Integer, String> {
        String token;
        private execute_submit_cert(String retrievedToken){
            token = retrievedToken;
        }
        @Override
        protected String doInBackground(String... args){
            String serverResponse;

            // socket host and port
            SocketConnection login = new SocketConnection("208.188.87.34.bc.googleusercontent.com", 47858);
            serverResponse = login.request(token);

            login.closeConnection();
            return serverResponse;

        }


        protected void onPostExecute(String result){
            // extract the cert from message received
            String encodedCert = getEncodedString(result);
            // decode and extract user's identification number
            String studentIC = extractSerialNumber(encodedCert);
            // login using user's identification number
            loginWithIC(studentIC);

        }
    }

}