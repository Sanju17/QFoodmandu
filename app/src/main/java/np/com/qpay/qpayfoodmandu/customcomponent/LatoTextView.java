package np.com.qpay.qpayfoodmandu.customcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import np.com.qpay.qpayfoodmandu.R;

/**
 * Created by Owner on 9/18/2016.
 */
public class LatoTextView extends TextView {

    private int textStyle;
    private Context context;

    public LatoTextView(Context context) {
        super(context);
        this.context = context;
    }

    public LatoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public LatoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attributeset) {
        initDefaultAttributes(attributeset);
        initCustomAttributes(attributeset);

    }

    private void initDefaultAttributes(AttributeSet attributeset) {
        textStyle = 1;
    }

    private void initCustomAttributes(AttributeSet attributeset) {
        TypedArray attributes =
                getContext().obtainStyledAttributes(attributeset, R.styleable.text_view);
        textStyle = attributes.getInt(R.styleable.text_view_textStyle1, 1);
        String fontPath = "";
        switch (textStyle) {
            case 0:
                fontPath = "fonts/Lato/Lato-Thin.ttf";
                break;
            case 1:
                fontPath = "fonts/Lato/Lato-Light.ttf";
                break;
            case 2:
                fontPath = "fonts/Lato/Lato-Medium.ttf";
                break;
            case 3:
                fontPath = "fonts/Lato/Lato-Regular.ttf";
                break;
            case 4:
                fontPath = "fonts/Lato/Lato-Bold.ttf";
                break;
            case 5:
                fontPath = "fonts/Lato/Lato-Italic.ttf";
                break;
            case 6:
                fontPath = "fonts/Lato/Lato-LightItalic.ttf";
                break;
            case 7:
                fontPath = "fonts/Lato/Lato-MediumItalic.ttf";
                break;
            case 8:
                fontPath = "fonts/Lato/Lato-BoldItalic.ttf";
                break;
            case 9:
                fontPath = "fonts/Lato/Lato-ThinItalic.ttf.ttf";
                break;
            case 10:
                fontPath = "fonts/Lato/Lato-Black.ttf";
                break;
        }
        Typeface face = Typeface.createFromAsset(context.getAssets(), fontPath);
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
