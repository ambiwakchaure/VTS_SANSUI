package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextViewDigital extends android.support.v7.widget.AppCompatTextView {

    public MyTextViewDigital(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyTextViewDigital(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyTextViewDigital(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "DS-DIGIB.TTF");
        setTypeface(tf);
    }
}
