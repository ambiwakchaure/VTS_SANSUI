package vts.snystems.sns.vts.classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vts.snystems.sns.vts.R;

/**
 * Created by sns003 on 22-Mar-18.
 */

public class M
{



    public static void t(String message)
    {
        Typeface tf = Typeface.createFromAsset(MyApplication.context.getAssets(), "TitilliumWeb-Regular.ttf");
        Toast toast = Toast.makeText(MyApplication.context, message, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTypeface(tf);
        toast.show();
    }
    public static void e(String key,String message)
    {
        Log.e(key,message);
    }
}
