package vts.snystems.sns.sansui.classes;

import android.graphics.Typeface;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sns003 on 22-Mar-18.
 */

public class M
{



    public static void t(String message)
    {
        /*Typeface tf = Typeface.createFromAsset(MyApplication.context.getAssets(), "TitilliumWeb-Regular.ttf");
        Toast toast = Toast.makeText(MyApplication.context, message, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTypeface(tf);
        toast.show();*/


        Toast.makeText(MyApplication.context, message, Toast.LENGTH_LONG).show();

    }
    public static void e(String key,String message)
    {
        Log.e(key,message);
    }
    public static void e(String message)
    {
        Log.e("BENOSYS_LOG",message);
    }
}
