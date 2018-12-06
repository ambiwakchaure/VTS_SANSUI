package vts.snystems.sns.vts.errorHandler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.ActivityLogin;
import vts.snystems.sns.vts.activity.HomeActivity;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;


public class ErrorActivity extends Activity implements AsyncResponse<String> {

	TextView error,send,tv_title_page;
    ImageView img_back;
    String errorE,jsonDetails;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.activity_error);


		error = (TextView) findViewById(R.id.error);
		send = (TextView) findViewById(R.id.send);
		tv_title_page = (TextView) findViewById(R.id.tv_title_page);
		img_back =(ImageView)findViewById(R.id.img_back);

		errorE = getIntent().getStringExtra("error");
		jsonDetails = getIntent().getStringExtra("json");

		Log.e("ERROR",""+errorE);

		error.setText(Html.fromHtml(errorE));
		send.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (F.checkConnection())
				{
					sendExpToServer(jsonDetails);
				}
				else
					{
					M.t("Oops ! no internet connection report will later");
				}


			}
		});
		
		img_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				System.exit(0);
				finish();
			}
		});

	}

	private void sendExpToServer(String jsonDetails)
	{
		try
		{
			String userName = MyApplication.prefs.getString(Constants.USER_NAME,"0");

			//Log.e("jsonDetails",jsonDetails);
			String[] parameters =
					{
							Constants.USER_NAME + "#" + userName,
							Constants.EXP_LOG + "#" + jsonDetails,
							Constants.createdDate + "#" + F.getSystemDateTime(),

					};
			Rc.withParamsProgress(
					new VolleyCallback()
					{
						@Override
						public void onSuccess(String result)
						{

							M.t("Error report sucessfully sent.");
							finish();
						}
					},
					new VolleyErrorCallback()
					{

						@Override
						public void onError(VolleyError volleyErrr)
						{
							M.t("Error report sucessfully sent.");
						}
					},

					Constants.webUrl + "" + Constants.insertAndroidLog,
					parameters,
					ErrorActivity.this, "first");

		} catch (Exception e) {

		}

	}


	private void SendErrorMail(Context _context, String ErrorContent )
	 {
//	  Intent sendIntent = new Intent(Intent.ACTION_SEND);
	  String subject ="CrashReport_vts_"+ MyApplication.prefs.getString(Constants.USER_NAME,"");
	  String body = "CrashReport_MailBody " +
	   "\n\n"+
	   ErrorContent+
	   "\n\n";
//	  sendIntent.putExtra(Intent.EXTRA_EMAIL,
//	    new String[] {"bodakesatish@gmail.com"});
//	  sendIntent.putExtra(Intent.EXTRA_TEXT, body);
//	  sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//	  sendIntent.setType("message/rfc822");
//	  _context.startActivity( Intent.createChooser(sendIntent, "Title:") );
		
		
		
		
		List<Intent> targetShareIntents=new ArrayList<Intent>();
        Intent shareIntent=new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfos=this.getPackageManager().queryIntentActivities(shareIntent, 0);
        if(!resInfos.isEmpty()){
            System.out.println("Have package");
            for(ResolveInfo resInfo : resInfos){
                String packageName=resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);
                /*if(packageName.contains("com.whatsapp") || packageName.contains("com.facebook.katana") || packageName.contains("com.google.android.gm")){*/
                    Intent intent=new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ambi.wakchaure@gmail.com"});
					intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                /*}*/
            }
            if(!targetShareIntents.isEmpty()){
                System.out.println("Have Intent");
                Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            }else{
                System.out.println("Do not Have Intent");

            }
        }

	 }
	

	public void processFinish(String output) {
		// TODO Auto-generated method stub
		
	}

	public void processFinishLog(String output) {
		
		
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		System.exit(0);
		finish();
	}

	public void goBack(View view) {

		finish();
	}
}
