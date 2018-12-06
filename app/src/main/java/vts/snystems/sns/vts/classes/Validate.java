package vts.snystems.sns.vts.classes;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.interfaces.Constants;

public class Validate
{

    public static boolean validateMobileNumber(EditText editText, String message)
    {
        boolean status = false;

        int length = editText.getText().toString().trim().length();
        if (length < 10 || (length > 10 && length < 13))
        {
            M.t(message);
            status = false;
        }
        else
        {
            if(length == 10)
            {
                status = true;
            }
            else if(length == 13)
            {
                String number = editText.getText().toString().trim();

                char plus = number.charAt(0);
                char fNumber = number.charAt(1);
                char sNumber = number.charAt(2);

                if(plus == '+' && fNumber != '+' && sNumber != '+')
                {
                    status = true;
                }
                else
                {
                    M.t(message);
                    status = false;
                }

            }

        }

        return status;
    }
    public static boolean validateVehicleNumber(String vehicleNumber,Context context)
    {


        //System.out.println(""+firstTwoCap);


        if(vehicleNumber.length() == 13)
        {
            String firstTwoCap = vehicleNumber.substring(0, 2);
            String firstSpace = vehicleNumber.substring(2, 3);
            String firstDigit = vehicleNumber.substring(3, 5);
            String secondSpace = vehicleNumber.substring(5, 6);
            String secondTwoCap = vehicleNumber.substring(6, 8);
            String thirdSpace = vehicleNumber.substring(8, 9);
            String lastDigits = vehicleNumber.substring(9, vehicleNumber.length());

            if(firstTwoCap == firstTwoCap.toUpperCase())
            {
                if(firstSpace.equals(" "))
                {


                    if(isNumeric(firstDigit))
                    {
                        if(secondSpace.equals(" "))
                        {


                            if(secondTwoCap == secondTwoCap.toUpperCase())
                            {
                                if(thirdSpace.equals(" "))
                                {
                                    if(!lastDigits.trim().isEmpty())
                                    {
                                        if(isNumeric(lastDigits))
                                        {
                                            return true;
                                        }
                                        else
                                        {

                                            M.t(context.getResources().getString(R.string.inval_num));
                                            return false;
                                        }
                                    }
                                    else
                                    {
                                        M.t(context.getResources().getString(R.string.inval_num));
                                        return false;
                                    }


                                }
                                else
                                {
                                    M.t(context.getResources().getString(R.string.inval_num));
                                    return false;
                                }

                            }
                            else
                            {
                                M.t(context.getResources().getString(R.string.inval_num));
                                return false;
                            }

                        }
                        else
                        {
                            M.t(context.getResources().getString(R.string.inval_num));
                            return false;
                        }

                    }
                    else
                    {
                        M.t(context.getResources().getString(R.string.inval_num));
                        return false;
                    }
                }
                else
                {
                    M.t(context.getResources().getString(R.string.inval_num));
                    return false;
                }

            }
            else
            {
                M.t(context.getResources().getString(R.string.inval_num));
                return false;
            }
        }
        else if(vehicleNumber.length() > 13)
        {
            M.t(context.getResources().getString(R.string.inval_length));
            return false;
        }
        else if(vehicleNumber.length() < 13)
        {
            M.t(context.getResources().getString(R.string.inval_length));
            return false;
        }
        else
        {
            M.t(context.getResources().getString(R.string.inval_length));
            return false;
        }

    }
    public static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
    public static boolean validateEmptyField(EditText editText, String message)
    {
        if (editText.getText().toString().trim().isEmpty())
        {
            M.t(message);
            return false;
        }
        else
        {
            return true;
        }


    }
    public static boolean validateEmptyField(TextView editText, String message)
    {
        if (editText.getText().toString().trim().isEmpty())
        {
            M.t(message);
            return false;
        }
        else
        {
            return true;
        }


    }
    public static boolean validateOverSpeed(EditText editText, String message)
    {
        if (editText.getText().toString().length() > 130)
        {
            M.t(message);
            return false;
        }
        else
        {
            return true;
        }


    }
    public static boolean validateMobileNumber(String editText, String message)
    {
        boolean status = false;

        int length = editText.length();
        if (length < 10 || (length > 10 && length < 13))
        {
            M.t(message);
            status = false;
        }
        else
        {
            if(length == 10)
            {
                status = true;
            }
            else if(length == 13)
            {
                String number = editText.trim();

                char plus = number.charAt(0);
                char fNumber = number.charAt(1);
                char sNumber = number.charAt(2);

                if(plus == '+' && fNumber != '+' && sNumber != '+')
                {
                    status = true;
                }
                else
                {
                    M.t(message);
                    status = false;
                }

            }

        }

        return status;
    }
    public static boolean checkNumberExist(String inputNumber, String message)
    {

        boolean status;
        try
        {
            String [] SOS_FC = MyApplication.prefs.getString(Constants.SOS_FC,"0").split("#");
            String [] SOS_SC = MyApplication.prefs.getString(Constants.SOS_SC,"0").split("#");
            String [] SOS_TC = MyApplication.prefs.getString(Constants.SOS_TC,"0").split("#");

            Log.e("SOS_CON","SOS_FC : "+SOS_FC);
            Log.e("SOS_CON","SOS_SC : "+SOS_SC);
            Log.e("SOS_CON","SOS_TC : "+SOS_TC);
            Log.e("SOS_CON","inputNumber : "+inputNumber);

            if (inputNumber.contains(SOS_FC[1]) || inputNumber.contains(SOS_SC[1]) || inputNumber.contains(SOS_TC[1]))
            {
                M.t(message);
                status = false;
            }
            else
            {
                status = true;
            }
        }
        catch (Exception e)
        {
            status = true;
        }
        return status;
    }

}
