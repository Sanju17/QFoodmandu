package np.com.qpay.qpayfoodmandu.customcomponent;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by deadlydragger on 6/13/16.
 */
public class DecimalTextMasking implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public DecimalTextMasking(EditText mEditText) {
        editTextWeakReference = new WeakReference<EditText>(mEditText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        EditText editTex = editTextWeakReference.get();
        if (!s.toString().equals(editTex.getText())) {
            editTex.removeTextChangedListener(this);
            String cleanString = s.toString().replaceAll("[$,.]", "");
            cleanString = cleanString.replace("NRS. ", "");
            double parsed;
            try {
                parsed = Double.parseDouble(cleanString.replaceAll("[^\\d]", ""));
            } catch (Exception e) {
                parsed = 0.00;
            }

            Locale locale = new Locale("en", "NP");

            NumberFormat formatter = NumberFormat.getNumberInstance(locale);
            DecimalFormat df = (DecimalFormat) formatter;
            df.applyPattern("#,##0.00");
            String formatted = df.format((parsed / 100));
            if (formatted.equals("0")) {
                formatted = "0.00";
            }
            editTex.setText(formatted);
            editTex.setSelection(formatted.length());
            editTex.addTextChangedListener(this);
        }
    }


    @Override
    public void afterTextChanged(Editable s) {

    }
}