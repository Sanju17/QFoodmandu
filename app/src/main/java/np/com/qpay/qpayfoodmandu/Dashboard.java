package np.com.qpay.qpayfoodmandu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import np.com.qpay.qpayfoodmandu.apicallers.HttpRequestHandler;
import np.com.qpay.qpayfoodmandu.commonconsts.CommonConsts;
import np.com.qpay.qpayfoodmandu.histroy.HistoryAdapter;
import np.com.qpay.qpayfoodmandu.histroy.HistoryInfo;
import np.com.qpay.qpayfoodmandu.sale.SaleamountActivity;

public class Dashboard extends AppCompatActivity {

    private SharedPreferences preferences;
    private String app_id = "";
    private String term_id = "";
    private HistoryAdapter historyAdapter;
    private RecyclerView recyclerView;
    private List<HistoryInfo> historyInfos = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        preferences = getSharedPreferences(CommonConsts.MY_PREFERENCES, Context.MODE_PRIVATE);
        String appHolderName = preferences.getString(CommonConsts.MERCHANT_NAME, "Foodmandu");
        app_id = preferences.getString(CommonConsts.APP_ID, "");
        term_id = preferences.getString(CommonConsts.MERCHANT_ENCRYPTED_ID, "");

        Toolbar appBar_tool = (Toolbar) findViewById(R.id.toolbar_custum);
        setSupportActionBar(appBar_tool);

        setTitle("Welcome " + appHolderName + "!");

        findViewById(R.id.sales_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, SaleamountActivity.class);
                startActivity(intent);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        new GetHistoryData().execute(); //Calling Async class.
    }

    private void setAdapter() {
        historyAdapter = new HistoryAdapter(historyInfos, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(historyAdapter);
    }

    class GetHistoryData extends AsyncTask<String, String, String> {
        String url = CommonConsts.MERCHANT_HISTORY_URL;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("AppId", app_id);
                jsonObject.put("id", term_id);
                response = HttpRequestHandler.postRequest(url, "", "", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d("amrit", "onPostExecute: " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                Boolean success = jsonObject.getBoolean("success");
                if (!status.isEmpty() && !status.equals("null") && status != null && status.equals("200") && success == true) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("History");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jRealData = jsonArray.getJSONObject(i);
                        HistoryInfo historyInfo = new HistoryInfo();
                        String Wallet = jRealData.getString("PaymentMethod");
                        String Txn_Amount = jRealData.getString("TrnAmount");
                        String Date = jRealData.getString("Date");
                        if (jRealData.getString("TrnType").equals("PU")) {
                            historyInfo.setAmount(Txn_Amount);
                            historyInfo.setDate(Date);
                            historyInfo.setWallet(Wallet);
                            historyInfos.add(historyInfo);
                        }
                    }
//                    historyAdapter.notifyDataSetChanged();
                    setAdapter();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
