package monash.fyp.attendanceapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;


/**
 * Created by kw.lee on 5/24/2017.
 */

public class SignetClientHelper {

    public static final String MISIGNET_ID_PACKAGE = "my.mimos.signetclient";
    public static final int REQUEST_REG_CODE = 1000;
    public static final int EXECUTE_REG_CODE = 1001;

    public static final int REQUEST_STORECERT_CODE = 2000;
    public static final int EXECUTE_STORECERT_CODE = 2001;

    public static final int REQUEST_DIGSIGN_CODE = 3000;
    public static final int EXECUTE_DIGSIGN_CODE = 3001;

    public static final int REQUEST_SUBMITCERT_CODE = 4002;
    public static final int EXECUTE_SUBMITCERT_CODE = 4003;

    public static final int REQUEST_DIGSIGN_HASH_CODE = 5000;
    public static final int EXECUTE_DIGSIGN_HASH_CODE = 5001;

    public static final int REQUEST_PROXYSIGN_HASH_CODE = 6000;
    public static final int EXECUTE_PROXYSIGN_HASH_CODE = 6001;

    public static final int REQUEST_VIEWCERT_CODE = 7000;
    public static final int REQUEST_REGSTATUS_CODE = 7001;

    // signet client status code for error message
    public static final String CONNECTION_ERROR = "400";
    public static final String INVALID_DATA_FORMAT = "401";
    public static final String INVALID_INTENT_ACTION = "402";
    public static final String UNAUTHORISED_APPLICATION = "403";
    public static final String PERMISSION_DENIED_BY_USER = "404";
    public static final String JKS_NOT_FOUND = "405";
    public static final String RSC_NOT_MATCH = "406";
    public static final String BASE64URL_DECODE_FAILURE = "407";
    public static final String PKI_GENERATION_FAILURE = "408";
    public static final String CSR_GENERATION_FAILURE = "409";
    public static final String SIGNATURE_GENERATION_FAILURE = "410";
    public static final String INVALID_KEYSTORE = "411";
    public static final String INVALID_CERTIFICATE = "412";
    public static final String SIGNATURE_EXPIRED = "413";
    public static final String INVALID_SIGNATURE = "414";
    public static final String CERTIFICATE_EXPIRED = "415";
    public static final String INVALID_CERTIFICATE_CHAIN = "416";
    public static final String INVALID_JWS_FORMAT = "417";
    public static final String SENSOR_DETECTION_FAILURE = "418";
    public static final String BACK_BUTTON_PRESSED = "419";
    public static final String JWS_SIGNATURE_GENERATION_FAILURE = "420";
    public static final String FILE_NOT_FOUND = "421";
    public static final String PUBLICKEY_NOT_MATCH = "422";
    public static final String NOT_IN_NEW_STATE = "423";
    public static final String NOT_IN_GENERATED_KEY_STATE = "424";
    public static final String NOT_IN_COMPLETED_REGISTRATION_STATE = "425";
    public static final String ACTION_DENIED_BY_USER = "426";
    public static final String ENCRYPTION_ERROR = "427";
    public static final String NAME_NOT_MATCH = "428";
    public static final String APP_ALREADY_REGISTERED = "429";
    public static final String SIGNATURE_VERIFIED_WITHOUT_KEYSTORE = "430";

    public Activity mActivity;


    public SignetClientHelper(Activity activity) {
        mActivity = activity;
    }

    /* get Authorisation request for registration from Mi-Signet Client
    /* @param name - Name of user to be put in the Common Name field of X.509 digital certificate
    /* @param ic - National ID of user to be put in the Subject serial number field of X.509 digital certificate
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for request registration
    */
    public void requestReg(String name, String ic, String appCert, int requestCode) {
        Intent intent = new Intent("signetclient.intent.action.REQUEST_REG");
        intent.putExtra("name", name);
        intent.putExtra("ic", ic);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Execution token for registration from Mi-Signet Client
    /* @param data - authorisation token received from Mi-Signet Server
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for execute registration
    */
    public void executeReg(String data, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.EXECUTE_REG");
        intent.putExtra("data", data);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Authorisation request for store certificate from Mi-Signet Client
    /* @param name - Common name of user used in CN field of certificate, of type String
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for request store certificate
    */
    public void requestStoreCert(String name, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.REQUEST_STORECERT");
        intent.putExtra("name", name);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Execution token for store certificate from Mi-Signet Client
    /* @param data - authorisation token received from Mi-Signet Server
    /* @param userCert - PEM format X.509 digitial certificate of user to be stored, of type String
    /* @param appCert - PEM format X.509 digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for execute store certificate
    */
    public void executeStoreCert(String data, String userCert, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.EXECUTE_STORECERT");
        intent.putExtra("data", data);
        intent.putExtra("usercert", userCert);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Authorisation request for digital signing on file from Mi-Signet Client
    /* @param filepath - Location of the file to be signed
    /* @param filename - Filename of the file to be signed
    /* @param mimeType - MIME type of the file to be signed
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for request digital signing on file
    */
    public void requestDigSign(String filepath, String filename, String mimeType, String appCert, int requestCode) {
        Intent intent = new Intent("signetclient.intent.action.REQUEST_DIGSIGN");
        intent.putExtra("appcert", appCert);
        intent.putExtra("filepath", filepath);
        intent.putExtra("filename", filename);
        intent.putExtra("mimetype", mimeType);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Execution token for digital signing on file from Mi-Signet Client
    /* @param data - authorisation token received from Mi-Signet Server
    /* @param filepath - Location of the file to be signed
    /* @param filename - Filename of the file to be signed
    /* @param appCert - PEM format X.509 digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for execute digital signing on file
    */
    public void executeDigSign(String data, String filepath, String filename, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.EXECUTE_DIGSIGN");
        intent.putExtra("data", data);
        intent.putExtra("filepath", filepath);
        intent.putExtra("filename", filename);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Authorisation request for digital signing on hash from Mi-Signet Client
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for request digital signing on hash
    */
    public void requestDigSignHash(String appCert, int requestCode) {
        Intent intent = new Intent("signetclient.intent.action.REQUEST_DIGSIGN_HASH");
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Execution token for digital signing on hash from Mi-Signet Client
    /* @param data - authorisation token received from Mi-Signet Server
    /* @param hash - Location of the file to be signed
    /* @param appCert - PEM format X.509 digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for execute digital signing on hash
    */
    public void executeDigSignHash(String data, String hash, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.EXECUTE_DIGSIGN_HASH");
        intent.putExtra("data", data);
        intent.putExtra("hash", hash);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    public void requestProxySignHash(String appCert, int requestCode) {
        Intent intent = new Intent("signetclient.intent.action.REQUEST_PROXYSIGN_HASH");
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    public void executeProxySignHash(String data, String hash, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.EXECUTE_PROXYSIGN_HASH");
        intent.putExtra("data", data);
        intent.putExtra("hash", hash);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }



    /* get Authorisation request for submit certificate from Mi-Signet Client
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for request submit certificate
    */
    public void requestSubmitCert(String appCert, int requestCode) {
        Intent intent = new Intent("signetclient.intent.action.REQUEST_SUBMITCERT");
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* get Execution token for submit certificate from Mi-Signet Client
    /* @param data - authorisation token received from Mi-Signet Server
    /* @param appCert - PEM format X.509 digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for execute submit certificate
    */
    public void executeSubmitCert(String data, String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.EXECUTE_SUBMITCERT");
        intent.putExtra("data", data);
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }

    /* View certificate in Mi-Signet Client
    /* @param appCert - PEM format digital certificate of the calling app for request authorisation
    /* @param requestCode - request code for request submit certificate
    */
    public void viewCert(String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.VIEW_CERT");
        intent.putExtra("appcert", appCert);

        mActivity.startActivityForResult(intent, requestCode);
    }


    public boolean isSignetInstalled() {

        boolean installed = false;
        try {
            PackageManager pm = mActivity.getPackageManager();
            pm.getPackageInfo(MISIGNET_ID_PACKAGE, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }

        return installed;
    }

    public void isSignetRegistered(String appCert, int requestCode) {

        Intent intent = new Intent("signetclient.intent.action.REG_STATUS");
        intent.putExtra("appcert", appCert);
        mActivity.startActivityForResult(intent, requestCode);
    }


}
