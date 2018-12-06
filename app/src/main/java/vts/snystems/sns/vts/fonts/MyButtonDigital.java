package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyButtonDigital extends android.support.v7.widget.AppCompatButton {

    public MyButtonDigital(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyButtonDigital(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyButtonDigital(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "digital-7.ttf");
        setTypeface(tf);
    }
}
