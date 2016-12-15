package np.com.qpay.qpayfoodmandu.sale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import np.com.qpay.qpayfoodmandu.Dashboard;
import np.com.qpay.qpayfoodmandu.R;
import np.com.qpay.qpayfoodmandu.apicallers.HttpRequestHandler;
import np.com.qpay.qpayfoodmandu.commonconsts.CommonConsts;
import np.com.qpay.qpayfoodmandu.customcomponent.QPayProgressDialog;
import np.com.qpay.qpayfoodmandu.qrcodegenerator.Contents;
import np.com.qpay.qpayfoodmandu.qrcodegenerator.QRCodeEncoder;
import np.com.qpay.qpayfoodmandu.utils.Decimalformate;
import np.com.qpay.qpayfoodmandu.utils.GlobalVariable;

/**
 * Created by deadlydragger on 10/4/16.
 */
public class Saleprocessing extends Fragment implements View.OnClickListener {

    TextView remainingTimeTextView;
    ProgressBar counterProgressBar;
    MyCountDownTimer myCountDownTimer;
    MyCountDownTimerprocess myCountDownTimerprocess;
    String term_id, app_id, dev_token;
    SharedPreferences preferences;
    Handler handler = new Handler();
    int count = 0;
    String amount;
    ImageView processImgView;
    RotateAnimation rotate;
    TextView process_condition, process_condition_details, status_process, amount_money;
    TextView cancel_sale;
    String cancel_stan, cancel_crrn;
    boolean cancel_ask=true;
    boolean cancel_first=true;
    TextView time_mins_sec;
    ImageView merchant_qr;
    TextView failure_msg;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(CommonConsts.MY_PREFERENCES, Context.MODE_PRIVATE);
        term_id = preferences.getString(CommonConsts.MERCHANT_ENCRYPTED_ID, "");
        app_id = preferences.getString(CommonConsts.APP_ID, "");
        dev_token = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        amount = new Decimalformate().decimalFormate(getArguments().getString("amount")) ;
        processImgView = (ImageView) view.findViewById(R.id.sale_loading);
        process_condition = (TextView) view.findViewById(R.id.process_condition);
        process_condition_details = (TextView) view.findViewById(R.id.process_condition_details);
        status_process = (TextView) view.findViewById(R.id.status);
        amount_money = (TextView) view.findViewById(R.id.amount);
        cancel_sale = (TextView) view.findViewById(R.id.cancel_sale);
        time_mins_sec = (TextView)view.findViewById(R.id.time_mins_sec);


        cancel_sale.setOnClickListener(this);
        rotate = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.anim_loading);
        processImgView.setAnimation(rotate);
        rotate.setRepeatCount(Animation.INFINITE);
        remainingTimeTextView = (TextView) view.findViewById(R.id.time_remaining_textview);
        String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(3 * 60 * 1000),
                TimeUnit.MILLISECONDS.toSeconds(3 * 60 * 1000) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(3 * 60 * 1000))
        );
        remainingTimeTextView.setText(time);
        counterProgressBar = (ProgressBar) view.findViewById(R.id.counter);
        counterProgressBar.setProgress(0);
        myCountDownTimer = new MyCountDownTimer(3 * 60 * 1000, 1000);
        myCountDownTimer.start();
        new Process_txn(dev_token, term_id, amount).execute();
        process_condition.setText("Initializing Payment");
        process_condition_details.setText("Your request transaction is being initializing.");
        status_process.setText("Initializing");
        amount_money.setText("NPR " + amount+"0");
        merchant_qr=(ImageView)view.findViewById(R.id.merchant_qr);
        create(term_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sale_processing, container, false);
    }
    public void vibration() {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }
    public void create(String code) {
        String qrInputText = null;
        String encryptedMsg = code;
        qrInputText = encryptedMsg;
        if (qrInputText != null) {
            //Find screen size
            WindowManager manager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                    null,
                    Contents.Type.TEXT,
                    BarcodeFormat.QR_CODE.toString(),
                    smallerDimension);
            try {
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                merchant_qr.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_sale:
                try {
                    cancel_ask=false;

                    new Canceltrxn(cancel_stan, cancel_crrn).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (((3 * 60 * 1000 - millisUntilFinished) * 100) / (3 * 60 * 1000));
            //Log.d("sangharsha","progress : " + progress);
            counterProgressBar.setProgress(progress);
            String time = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            );
            if (progress >= 90){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    counterProgressBar.setProgressDrawable(getActivity().getDrawable(R.drawable.progressbar_last_end));
                }
            }else if (progress >= 97){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    counterProgressBar.setProgressDrawable(getActivity().getDrawable(R.drawable.progressbar_end));
                }
            }
            remainingTimeTextView.setText(time);
        }

        @Override
        public void onFinish() {
            cancel_ask=false;
            remainingTimeTextView.setText("00:00");
            counterProgressBar.setProgress(100);
            transctionfail();
        }
    }
    public class MyCountDownTimerprocess extends CountDownTimer {
        public MyCountDownTimerprocess(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (((90 * 1000 - millisUntilFinished) * 100) / (90 * 1000));
//            Log.d("sangharsha","progress proceed : " + progress);
            counterProgressBar.setProgress(progress);
            String time = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            );
            if (progress >= 90){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && counterProgressBar != null) {
                    counterProgressBar.setProgressDrawable(getActivity().getDrawable(R.drawable.progressbar_last_end));
                }
            }else if (progress >= 97 && counterProgressBar != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    counterProgressBar.setProgressDrawable(getActivity().getDrawable(R.drawable.progressbar_end));
                }
            }
            remainingTimeTextView.setText(time);
        }

        @Override
        public void onFinish() {
            cancel_ask=false;
            remainingTimeTextView.setText("00:00");
            counterProgressBar.setProgress(100);
            transctionfail();
        }
    }

    public class Process_txn extends AsyncTask<String, String, String> {
        String device_token, lat, lng, term_id;

        String amount;

        public Process_txn(String device_token, String term_id, String amount) {
            this.device_token = device_token;
            this.term_id = term_id;
            this.amount = amount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            //HttpUrlConnectionPost httpUrlConnectionPost = new HttpUrlConnectionPost();
            String result_process = null;
            try {
                jsonObject.put("app_id", app_id);
                jsonObject.put("dev_token", device_token);
                jsonObject.put("term_id", term_id);
                jsonObject.put("txn_amt", amount);
                jsonObject.put("lat", ((GlobalVariable) getActivity().getApplication()).getLat());
                jsonObject.put("lng", ((GlobalVariable) getActivity().getApplication()).getLng());
                Log.d("sangharsha", "process trxn : " + jsonObject.toString());
                result_process = HttpRequestHandler.postRequest(CommonConsts.PROCESS_LOG_TXN,"","", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result_process;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("sangharsha", "onPostExecute: " + s);
            super.onPostExecute(s);
            try {
                JSONObject LogTxnResult = new JSONObject(s);
                JSONObject jsonObject = LogTxnResult.getJSONObject("LogTxnResult");
                String status = jsonObject.getString("status");
                String stan = jsonObject.getString("stan");
                String crrn = jsonObject.getString("crrn");
                cancel_stan = stan;
                cancel_crrn = crrn;
                if (status.equals("00")) {
                    loop(stan, String.valueOf(amount), crrn);
//                        new ChecktxnStatusm(tran_id).execute();
                } else if (status.equals("99")) {
                    myCountDownTimer.cancel();
                    transctionfail();
                }
            } catch (Exception e) {
                rotate.cancel();
                myCountDownTimer.cancel();

                e.printStackTrace();
                transctionfail();

            }

        }
    }

    public void loop(final String tran, final String amount, final String crrn) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                Log.d("sangharsha", String.valueOf(count));
                new ChecktxnStatusm(tran, amount, crrn).execute();
            }
        }, 5000);
    }

    public class ChecktxnStatusm extends AsyncTask<String, String, String> {
        String tran_id, amount, crrn;

        public ChecktxnStatusm(String tran_id, String amount, String crrn) {
            this.tran_id = tran_id;
            this.amount = amount;
            this.crrn = crrn;
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            String chechtxnstatusm = null;
            //HttpUrlConnectionPost httpUrlConnectionPost = new HttpUrlConnectionPost();
            try {
                if (cancel_ask){
                    jsonObject.put("app_id", app_id);
                    jsonObject.put("dev_token", dev_token);
                    jsonObject.put("term_id", term_id);
                    jsonObject.put("stan", tran_id);
                    jsonObject.put("crrn", crrn);
                    chechtxnstatusm = HttpRequestHandler.postRequest(CommonConsts.CHECK_TRANSCTION_STATUSM,"" ,"" , jsonObject);
                    Log.d("sangharsha", chechtxnstatusm);
                    Log.d("sangharsha", jsonObject.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return chechtxnstatusm;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject CheckTxnStatusMResult = jsonObject.getJSONObject("CheckTxnStatusMResult");
                String apr_code = CheckTxnStatusMResult.getString("apr_code");
                String status = CheckTxnStatusMResult.getString("status");
                if (status.equals("00")) {

                    if (apr_code.equals(CommonConsts.LO)) {
                        loop(tran_id, amount, crrn);
                    } else if (apr_code.equals(CommonConsts.RT)) {
                        cancel_sale.setVisibility(View.GONE);
                        process_condition.setText("Received Payment");
                        process_condition_details.setText("Your request transaction is Received.");
                        status_process.setText("Receiving");

                        loop(tran_id, amount, crrn);

                    } else if (apr_code.equals(CommonConsts.PR)) {
                        cancel_sale.setVisibility(View.GONE);
                        process_condition.setTextColor(Color.parseColor("#A5DC86"));
                        status_process.setTextColor(Color.parseColor("#A5DC86"));
                        process_condition.setText("Proceed Payment");
                        process_condition_details.setText("Your request transaction is Proceed.");
                        status_process.setText("Processing");
                        loop(tran_id, amount, crrn);
                        String time = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes( 90 * 1000),
                                TimeUnit.MILLISECONDS.toSeconds(90 * 1000) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(90 * 1000))
                        );
                        time_mins_sec.setText(" MINS");


                        if (cancel_first==true){
                            cancel_first=false;
                            remainingTimeTextView.setText(time);
                            myCountDownTimerprocess = new MyCountDownTimerprocess(90 * 1000, 1000);
                            myCountDownTimer.cancel();
                            counterProgressBar.setProgress(0);
                            myCountDownTimerprocess.start();
                        }


                    } else if (apr_code.equals("00")) {
                        vibration();
                        if(myCountDownTimerprocess != null) {
                            myCountDownTimerprocess.cancel();
                            myCountDownTimerprocess = null;
                        }
                        myCountDownTimer.cancel();
                        rotate.cancel();
                        count = 0;
                        transctionsucess();
                    } else if (apr_code.equals("CN")) {
                        if(myCountDownTimerprocess != null) {
                            myCountDownTimerprocess.cancel();
                            myCountDownTimerprocess = null;
                        }
                        if(myCountDownTimer != null) {
                            myCountDownTimer.cancel();
                            myCountDownTimer = null;
                        }
                        rotate.cancel();
                        transctionfail();
                    } else {
                        myCountDownTimer.cancel();
                        rotate.cancel();
                        transctionfail();
                    }
                } else {
                    myCountDownTimer.cancel();
                    rotate.cancel();
                    transctionfail();
                }
            } catch (Exception e) {
                e.printStackTrace();
                myCountDownTimer.cancel();

        }


        }
    }

    public void transctionfail() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_custom_dialog_transaction_failure, null);
        final TextView failure_msg=(TextView)v.findViewById(R.id.failure_msg);
        failure_msg.setText("Your current transaction for Amount NPR "+amount+" could not be completed successfully.");
        builder.setView(v);
        builder.setCancelable(false);
        final Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        v.findViewById(R.id.transaction_failure_btn_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                getActivity().onBackPressed();
                startActivity(new Intent(getActivity(),Dashboard.class));
                getActivity().finish();
            }
        });
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density = getResources().getDisplayMetrics().density;
        lp.width = (int) (320 * density);
        lp.height = (int) (360 * density);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(lp);
    }
    public void transctionfailfinish() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_custom_dialog_transaction_failure, null);
        builder.setView(v);
        builder.setCancelable(false);
        final Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        v.findViewById(R.id.transaction_failure_btn_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(),Dashboard.class));
                getActivity().finish();
            }
        });
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density = getResources().getDisplayMetrics().density;
        lp.width = (int) (320 * density);
        lp.height = (int) (360 * density);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(lp);
    }

    public void transctionsucess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_custom_dialog_transaction_succeed, null);
        final TextView success_msg = (TextView)v.findViewById(R.id.success_msg);
        success_msg.setText("You have successfully received Amount NPR "+amount);
        builder.setView(v);
        builder.setCancelable(false);
        final Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        v.findViewById(R.id.transaction_succeed_btn_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(), Dashboard.class));
                getActivity().finish();
            }
        });
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density = getResources().getDisplayMetrics().density;
        lp.width = (int) (320 * density);
        lp.height = (int) (360 * density);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(lp);
    }

    public class Canceltrxn extends AsyncTask<String, String, String> {
        String stan, crrn;
        QPayProgressDialog qprogressdialogwhite;

        public Canceltrxn(String stan, String crrn) {
            this.stan = stan;
            this.crrn = crrn;

        }

        @Override
        protected String doInBackground(String... params) {
//            HttpUrlConnectionJson httpUrlConnectionJson = new HttpUrlConnectionJson();
            String cancel_txn = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_id", app_id);

                jsonObject.put("term_id", term_id);
                jsonObject.put("stan", stan);
                jsonObject.put("crrn", crrn);
                Log.d("sangharsha", "cancel txn post : " + jsonObject);
                cancel_txn = HttpRequestHandler.postRequest(CommonConsts.CANCEL_MERCH_TRXN, "", "", jsonObject);
                Log.d("sangharsha", "cancel txn response  : " + cancel_txn);


            } catch (Exception e) {
                e.printStackTrace();

            }
            return cancel_txn;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                qprogressdialogwhite.dismiss();
//                getActivity().onBackPressed();
                Intent i = new Intent(getActivity(), Dashboard.class);
                startActivity(i);
                getActivity().finish();
            } catch (Exception e) {
                qprogressdialogwhite.dismiss();
                e.printStackTrace();

            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            qprogressdialogwhite= new QPayProgressDialog(getActivity());
            qprogressdialogwhite.show();


        }
    }
}
