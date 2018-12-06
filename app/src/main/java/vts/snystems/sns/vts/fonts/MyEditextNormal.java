package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by snsystem_amol on 28/06/2017.
 */

public class MyEditextNormal extends android.support.v7.widget.AppCompatEditText {

    //vts.snystems.sns.vts.fonts.MyTextViewNormal
    public MyEditextNormal(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyEditextNormal(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyEditextNormal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "TitilliumWeb-Regular.ttf");
        setTypeface(tf);
    }
}
