package np.com.qpay.qpayfoodmandu.sale;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import np.com.qpay.qpayfoodmandu.R;
import np.com.qpay.qpayfoodmandu.customcomponent.QPayProgressDialog;

/**
 * Created by deadlydragger on 9/1/16.
 */
public class SaleamountActivity extends AppCompatActivity implements  Onclick{
    EditText mAmount;
    Toolbar appBar_tool;
    Handler handler = new Handler();
    int count = 0;
    SharedPreferences preferences;
    String term_id, app_id, dev_token, amount = "";

    Button process;
    QPayProgressDialog progressDialog;
    Dialog dialog;
    Saleprocessfragment saleprocessfragment ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_processing);
        appBar_tool = (Toolbar) findViewById(R.id.toolbar_custum);
        setSupportActionBar(appBar_tool);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sale Processing");
        saleprocessfragment = new Saleprocessfragment();
        saleprocessfragment.setOnclick(this);
        appBar_tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.process_fragment,saleprocessfragment)
                .commit();

    }

    @Override
    public void onBackPressed() {
        if (saleprocessfragment != null){
            super.onBackPressed();
        }else {

        }
}

    @Override
    public void Onclick(String amount) {
        getSupportFragmentManager().beginTransaction()
                .remove(saleprocessfragment).commit();
        saleprocessfragment=null;
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        Saleprocessing saleprocessing = new Saleprocessing();
        fragmentTransaction .replace(R.id.process_fragment,saleprocessing);
        Bundle bundle = new Bundle();
        bundle.putString("amount",amount);
        saleprocessing.setArguments(bundle);
        fragmentTransaction.commit();
    }
}
