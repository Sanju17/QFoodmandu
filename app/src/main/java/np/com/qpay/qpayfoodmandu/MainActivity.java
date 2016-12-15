package np.com.qpay.qpayfoodmandu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import np.com.qpay.qpayfoodmandu.apicallers.HttpRequestHandler;
import np.com.qpay.qpayfoodmandu.commonconsts.CommonConsts;
import np.com.qpay.qpayfoodmandu.customcomponent.GPSTracker;
import np.com.qpay.qpayfoodmandu.customcomponent.LatoTextView;
import np.com.qpay.qpayfoodmandu.customcomponent.QPayProgressDialog;
import np.com.qpay.qpayfoodmandu.utils.GlobalVariable;
import np.com.qpay.qpayfoodmandu.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private LinearLayout qrcodeScannerLayout;
    private LinearLayout noInternetConnectionLayout;
    private LinearLayout loginLayout;
    private CompoundBarcodeView barcodeView;
    private LatoTextView restroNameTextView;
    private boolean isTableInfoChecked = false;
    private String scanContent;
    private float mDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(CommonConsts.MY_PREFERENCES, Context.MODE_PRIVATE);

        getLocation();

        //Check if permission is set if not ask for permission
        Utils.checkAndRequestPermissions(MainActivity.this);

        //get density of the device
        mDensity = getResources().getDisplayMetrics().density;

        //QR Code configuration
        qrcodeScannerLayout = (LinearLayout) findViewById(R.id.barcode_scanner_layout);
        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner_view);
        barcodeView.setStatusText("Place the QRCode in the Scanner");
        hideQRCodeLayout();

        //Login layout
        loginLayout = (LinearLayout) findViewById(R.id.login_main_layout);
        restroNameTextView = (LatoTextView) findViewById(R.id.restro_name_text_view);
        restroNameTextView.setText(sharedPreferences.getString(CommonConsts.MERCHANT_NAME, "Foodmandu").toUpperCase());
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(MainActivity.this) && Utils.isInternetAvailable()) {
                    execLogin();
                } else {
                    showErrorDialog("No Internet Connection!!!");
                }
            }
        });
        hideLoginlayout();

        //no internet connection layout
        noInternetConnectionLayout = (LinearLayout) findViewById(R.id.no_internet_connection_id);
        findViewById(R.id.no_internet_connection_try_again_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAppropriateLayout();
            }
        });
        hideNoInternetConnectionLayout();

        showAppropriateLayout();
//        Utils.dismissProgressDialog(qPayProgressDialog);
    }

    public void getLocation() {
        try {
            GPSTracker gps = new GPSTracker(MainActivity.this);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                if (latitude != 0.0 && longitude != 0.0) {
                    ((GlobalVariable) MainActivity.this.getApplication()).setLat(latitude);
                    ((GlobalVariable) MainActivity.this.getApplication()).setLng(longitude);
                    Log.d("sangharsha", "lat lon from splash : " + latitude + longitude);
                }


            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * show the layout according to condition
     */

    private void showAppropriateLayout() {
        if (Utils.isNetworkAvailable(MainActivity.this) && Utils.isInternetAvailable()) {
            if (sharedPreferences.contains(CommonConsts.IS_MERCHANT_REGISTERED)) {
                showLoginlayout();
            } else {
                showQRCodeLayout();
                barcodeView.decodeSingle(callback);
            }
        } else {
            showNoInternetConnectionLayout();
        }
    }

    private void showSecurityCodeDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_pin_enter, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);  //show keyboard automatically in dialog
        dialog.setCancelable(false);

        final EditText editText = (EditText) dialogView.findViewById(R.id.pin_id_enter);
        (dialogView.findViewById(R.id.set_local_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().isEmpty()) {
                    new Check_merchant_status(scanContent, editText.getText().toString()).execute();
                    dialog.dismiss();
                }else {
                    Toast.makeText(MainActivity.this, "Please Enter Security code!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density = getResources().getDisplayMetrics().density;
        lp.width = (int) (480 * density);
        lp.height = (int) (350 * density);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * Process Login
     */
    private void execLogin() {
        sharedPreferences = getSharedPreferences(CommonConsts.MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CommonConsts.IS_LOGGED_IN, true);
        editor.commit();
        editor.apply();
        Intent intent = new Intent(MainActivity.this, Dashboard.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            scanContent = result.getText();
            Toast.makeText(MainActivity.this, scanContent, Toast.LENGTH_SHORT).show();
            showSecurityCodeDialog();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    /**
     * Show QR Code Scanner Layout
     */

    private void showQRCodeLayout() {
        qrcodeScannerLayout.setVisibility(View.VISIBLE);
        qrcodeScannerLayout.setClickable(true);
    }

    /**
     * Hide QR Code Scanner Layout
     */

    private void hideQRCodeLayout() {
        qrcodeScannerLayout.setVisibility(View.GONE);
        qrcodeScannerLayout.setClickable(false);
    }

    /**
     * Hide Login Layout
     */

    private void hideLoginlayout() {
        loginLayout.setVisibility(View.GONE);
        loginLayout.setClickable(false);
    }

    /**
     * Show Login Layout
     */
    private void showLoginlayout() {
        loginLayout.setVisibility(View.VISIBLE);
        loginLayout.setClickable(true);
    }

    /**
     * Hide No internet connection Layout
     */
    private void hideNoInternetConnectionLayout() {
        noInternetConnectionLayout.setVisibility(View.GONE);
        noInternetConnectionLayout.setClickable(false);
    }

    /**
     * Show No internet connection Layout
     */
    private void showNoInternetConnectionLayout() {
        noInternetConnectionLayout.setVisibility(View.VISIBLE);
        noInternetConnectionLayout.setClickable(true);
    }

    public class Check_merchant_status extends AsyncTask<String, String, String> {
        String term_id, sec_code;
        private QPayProgressDialog progressDialog;

        public Check_merchant_status(String term_id, String sec_code) {
            this.term_id = term_id;
            this.sec_code = sec_code;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new QPayProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            String result_checl_status = null;
            try {
                jsonObject.put("term_id", term_id);
                jsonObject.put("sec_code", sec_code);
                jsonObject.put("lat", ((GlobalVariable) MainActivity.this.getApplication()).getLat());
                jsonObject.put("lng", ((GlobalVariable) MainActivity.this.getApplication()).getLng());
                Log.d("sangharsha", jsonObject.toString());
                result_checl_status = HttpRequestHandler.postRequest(CommonConsts.CHECK_STATUS_MERCHANT, "", "",jsonObject);
                Log.d("sangharsha", result_checl_status);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result_checl_status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s.isEmpty()) {
                barcodeView.decodeSingle(callback);
                showErrorDialog("Internet Connection Problem.");
            } else {
                try {
                    JSONObject act = new JSONObject(s);
                    JSONObject ActivateMerchantResult = act.getJSONObject("ActivateMerchantResult");
                    String status = ActivateMerchantResult.getString("status");
                    if (status != null && !status.isEmpty() && !status.equals("null") && status.equals("00")) {
                        String mcc = ActivateMerchantResult.getString("mcc");
                        String merch_id = ActivateMerchantResult.getString("merch_id");
                        String merch_name = ActivateMerchantResult.getString("merch_name");
                        String term_id = ActivateMerchantResult.getString("enc_merch_id");
                        String taxi_flag = ActivateMerchantResult.getString("taxi_flag");
                        String app_id = ActivateMerchantResult.getString("app_id");
                        String cell_phone = ActivateMerchantResult.getString("cell_phone");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(CommonConsts.MERCHANT_ID, scanContent);

                        //Merchant Info
                        editor.putString(CommonConsts.MERCHANT_NAME, merch_name);
                        editor.putString(CommonConsts.MERCHANT_ID, merch_id);
                        editor.putString(CommonConsts.MERCHANT_ENCRYPTED_ID, term_id);
                        editor.putString(CommonConsts.TAXI_FLAG, taxi_flag);
                        editor.putString(CommonConsts.APP_ID, app_id);
                        editor.putString(CommonConsts.MERCHANT_CELL_PHONE, cell_phone);
                        editor.putString(CommonConsts.MERCHANT_MCC, mcc);

                        editor.putString(CommonConsts.IS_MERCHANT_REGISTERED, "true");
                        editor.commit();
                        editor.apply();
                        hideQRCodeLayout();
                        isTableInfoChecked = true;
                        restroNameTextView.setText(sharedPreferences.getString(CommonConsts.MERCHANT_NAME, "Foodmandu").toUpperCase());
                        showAppropriateLayout();
                    } else if (status != null && !status.isEmpty() && !status.equals("null") && status.equals("99")) {
                        Toast.makeText(MainActivity.this, "Failed to reister", Toast.LENGTH_LONG).show();
                        barcodeView.decodeSingle(callback);
                    }else {
                        showErrorDialog(act.getString("message"));
                        barcodeView.decodeSingle(callback);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorDialog("Something went wrong. Please contact administrator.");
                }
            }

        }
    }

    /**
     * Show Error dialog
     */
    private void showErrorDialog(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
