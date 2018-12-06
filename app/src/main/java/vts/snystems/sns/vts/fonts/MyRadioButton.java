package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyRadioButton extends android.support.v7.widget.AppCompatRadioButton {

    public MyRadioButton(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "TitilliumWeb-Regular.ttf");
        setTypeface(tf);
    }
}
