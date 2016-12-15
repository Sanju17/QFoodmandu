package np.com.qpay.qpayfoodmandu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import np.com.qpay.qpayfoodmandu.MainActivity;
import np.com.qpay.qpayfoodmandu.commonconsts.CommonConsts;
import np.com.qpay.qpayfoodmandu.customcomponent.QPayProgressDialog;

/**
 * Created by Owner on 9/16/2016.
 */
public class Utils {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public static boolean checkAndRequestPermissions(Context context) {
        int permissionSendMessage = ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int internetPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int externalStoragewrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readInternalStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int callPhone = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        int contact = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (contact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (callPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (externalStoragewrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readInternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED)
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isInternetAvailable() {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        InetAddress inetAddress1 = InetAddress.getByName("google.com");
                        return inetAddress1;
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(20000, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }

    public static QPayProgressDialog showProgressDialog(Context context) {
        QPayProgressDialog qPayProgressDialog = new QPayProgressDialog(context);
        qPayProgressDialog.show();
        return qPayProgressDialog;
    }

    public static void dismissProgressDialog(QPayProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth() - 100;
        location.bottom = location.top + v.getHeight();
        return location;
    }

    public static String parseDateToddMMyyyy(String inputPattern, String outputPattern, String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String setFormattedText(String s) {
        boolean isNegetive = false;
        if (s.contains("-")) {
            isNegetive = true;
        }
        if (s.contains(".")) {
            if (s.substring(s.indexOf(".") + 1).length() == 1) {
                s += "0";
            } else {
                s = String.valueOf(new BigDecimal(Double.parseDouble(s)).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
        } else {
            s = s + ".00";
        }

        String cleanString = s.toString().replaceAll("[$,.]", "");
        cleanString = cleanString.replace("NPR. ", "");

        Locale locale = new Locale("en", "NP");
        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        DecimalFormat df = (DecimalFormat) formatter;
        df.applyPattern("#,##0.00");

        double parsed;
        try {
            parsed = Double.parseDouble(cleanString.replaceAll("[^\\d]", ""));
        } catch (Exception e) {
            parsed = 0.00;
        }

        String formatted = df.format((parsed / 100));
        if (isNegetive) {
            formatted = "-" + formatted;
        }

        formatted = "NPR. " + formatted;
        return formatted;
    }


    public static void logOut(Context context, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CommonConsts.IS_LOGGED_IN, false);
        editor.commit();
        editor.apply();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("logout", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}