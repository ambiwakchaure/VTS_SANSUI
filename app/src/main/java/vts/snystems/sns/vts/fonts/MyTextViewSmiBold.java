package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextViewSmiBold extends android.support.v7.widget.AppCompatTextView {

    public MyTextViewSmiBold(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyTextViewSmiBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyTextViewSmiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "TitilliumWeb-SemiBold.ttf");
        setTypeface(tf);
    }
}
