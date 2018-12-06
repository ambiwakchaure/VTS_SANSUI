package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextViewNormal extends android.support.v7.widget.AppCompatTextView {

    public MyTextViewNormal(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyTextViewNormal(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyTextViewNormal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "TitilliumWeb-Regular.ttf");
        setTypeface(tf);
    }
}
