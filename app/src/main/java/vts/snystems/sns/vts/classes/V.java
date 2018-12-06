package vts.snystems.sns.vts.classes;

import android.util.Log;
import android.widget.TextView;

public class V
{

    public static boolean emptyTextView(TextView textView, String data,String message)
    {
        if (textView.getText().toString().equals(data))
        {
            M.t(message);
            return false;
        }
        else
        {
            return true;
        }


    }
    public static boolean checkNull(String stringData)
    {
        if (stringData.equals("null") || stringData.equals("NULL") || stringData.equals("") || stringData.equals("NA") || stringData.equals("0"))
        {
             Log.e("NULL_EXEPTION","Null data found");
            return false;
        }
        else
        {
            return true;
        }


    }
}
