package np.com.qpay.qpayfoodmandu.sale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import np.com.qpay.qpayfoodmandu.Dashboard;
import np.com.qpay.qpayfoodmandu.R;
import np.com.qpay.qpayfoodmandu.commonconsts.CommonConsts;
import np.com.qpay.qpayfoodmandu.customcomponent.DecimalTextMasking;

/**
 * Created by deadlydragger on 10/4/16.
 */
public class Saleprocessfragment extends Fragment implements View.OnClickListener {
    SharedPreferences preferences;
    String term_id,app_id,dev_token;
    EditText mAmount;
    Button process;
    Onclick onclick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_processing_amount,container,false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setGloballayoutListener(view);
        mAmount = (EditText) view.findViewById(R.id.amount_id);
        process = (Button)view. findViewById(R.id.process);
        process.setOnClickListener(this);
        //To get Keyboard height
        mAmount.requestFocus();
//        showKeyboard();
           setKeyboardFocus(mAmount);

        mAmount.setFocusableInTouchMode(true);
        mAmount.addTextChangedListener(new DecimalTextMasking(mAmount));



    }
    public static void setKeyboardFocus(final EditText primaryTextField) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
            }
        }, 100);
    }
    private void setGloballayoutListener(final View view) {
        view.findViewById(R.id.root_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                View rootView =view.findViewById(R.id.root_layout).getRootView();

                rootView.getWindowVisibleDisplayFrame(r);
                DisplayMetrics dm = rootView.getResources().getDisplayMetrics();

                int heightDiff = rootView.getBottom() - r.bottom;
//                Log.d("Amrit", "" + heightDiff);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) (view.findViewById(R.id.root_layout).getLayoutParams());
                params.bottomMargin = heightDiff;
                view.findViewById(R.id.root_layout).setLayoutParams(params);
            }
        });
    }
    public void setOnclick(Onclick onclick){
        this.onclick=onclick;
    }
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(CommonConsts.MY_PREFERENCES, Context.MODE_PRIVATE);
        term_id = preferences.getString(CommonConsts.MERCHANT_ENCRYPTED_ID, "");
        app_id = preferences.getString(CommonConsts.APP_ID, "");
        dev_token = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.process:
                try {
                    double amount = Double.valueOf(mAmount.getText().toString().replaceAll(",",""));
                    if (amount >= 10 && amount <= 10000){
                        hideKeyboard();
                       onclick.Onclick(String.valueOf(amount));

                    }else {
                        mAmount.setError("Enter amount in range of 10 to 10,000");
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(), Dashboard.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }
}
