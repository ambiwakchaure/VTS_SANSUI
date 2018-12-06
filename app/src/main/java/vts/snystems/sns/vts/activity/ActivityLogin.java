package vts.snystems.sns.vts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.Validate;
import vts.snystems.sns.vts.errorHandler.ExceptionHandler;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class ActivityLogin extends AppCompatActivity {

    @BindView(R.id.usernameEditText)
    EditText usernameEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.rememberPasswordCheckBox)
    CheckBox rememberPasswordCheckBox;


    @BindView(R.id.loginButton)
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.loginButton)
    public void onViewClicked()
    {
        if (!Validate.validateEmptyField(usernameEditText, VMsg.userName))
        {
            return;
        }
        if (!Validate.validateEmptyField(passwordEditText, VMsg.userPassword))
        {
            return;
        }
        if (F.checkConnection())
        {
            proceedLogin();
        }
        else {
            M.t("Please check internet connection or wifi connection.");
        }
    }


    public void proceedLogin() {

        try
        {
            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + usernameEditText.getText().toString().trim(),
                            Constants.PASSWORD + "#" + passwordEditText.getText().toString().trim()

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            parseLoginResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr)
                        {
                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Oops ! request timed out.", "Server Time out");
                            }
                            else
                            {
                                F.handleError(volleyErrr,ActivityLogin.this,"Webservice : Constants.login,Function : proceedLogin()");
                            }


                        }
                    },
                    Constants.webUrl + "" + Constants.validateLogin,
                    parameters,
                    ActivityLogin.this, "first");


        } catch (Exception e) {

        }

    }

    private void parseLoginResponse(String loginJson) {
        try {

            if (loginJson != null || loginJson.length() > 0) {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject) {

                    JSONObject loginJsonObject = new JSONObject(loginJson);

                    String success = loginJsonObject.getString("success");
                    String message = loginJsonObject.getString("message");
                    Log.d("login", "----E");
                    if (success.equals("1")) {
                        M.t(message);

                        if (rememberPasswordCheckBox.isChecked())
                        {
                            MyApplication.editor.putString(Constants.REMEMBER_PASSWORD, "remember").commit();
                        }
                        else
                        {
                            MyApplication.editor.putString(Constants.REMEMBER_PASSWORD, "not_remember").commit();

                        }
                        MyApplication.editor.putString(Constants.USER_NAME, usernameEditText.getText().toString().trim()).commit();
                        MyApplication.editor.putString(Constants.PASSWORD, passwordEditText.getText().toString().trim()).commit();
                        MyApplication.editor.putString(Constants.NOTI_ALERT,"on").commit();
                        MyApplication.editor.putBoolean(Constants.SERVICE_FLAG,false).commit();


                        Intent i = new Intent(ActivityLogin.this, HomeActivity.class);
                        startActivity(i);
                        finish();

                    } else if (success.equals("2")) {
                        M.t(message);
                    } else if (success.equals("3")) {
                        M.t(message);
                    }
                    /*else if (success.equals("0"))
                    {
                        M.t(message);
                    }*/
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sR(String message, String error) {
        new MaterialDialog.Builder(ActivityLogin.this)
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        proceedLogin();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                    }
                })
                .show();


    }


}
