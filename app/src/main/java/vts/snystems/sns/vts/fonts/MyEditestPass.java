package vts.snystems.sns.vts.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

public class MyEditestPass extends ShowHidePasswordEditText {

    public MyEditestPass(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MyEditestPass(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MyEditestPass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "TitilliumWeb-Regular.ttf");
        setTypeface(tf);
    }
}
