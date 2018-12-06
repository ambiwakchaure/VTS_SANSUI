package vts.snystems.sns.vts.errorHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements
		Thread.UncaughtExceptionHandler {
	private final Activity myContext;
	private final String LINE_SEPARATOR = "\n";

	public ExceptionHandler(Activity context) {
		myContext = context;
		
	}

	public void uncaughtException(Thread thread, Throwable exception) {


		try
		{

			StringWriter stackTrace = new StringWriter();
			exception.printStackTrace(new PrintWriter(stackTrace));

			String exceptionLog = stackTrace.toString();

			StringBuilder errorReport = new StringBuilder();

			errorReport.append("Exeption: "+exceptionLog);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Brand: ");
			errorReport.append(Build.BRAND);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Device: ");
			errorReport.append(Build.DEVICE);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Model: ");
			errorReport.append(Build.MODEL);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Id: ");
			errorReport.append(Build.ID);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Product: ");
			errorReport.append(Build.PRODUCT);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("SDK: ");
			errorReport.append(Build.VERSION.SDK_INT);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Release: ");
			errorReport.append(Build.VERSION.RELEASE);
			errorReport.append(LINE_SEPARATOR);
			errorReport.append("Incremental: ");
			errorReport.append(Build.VERSION.INCREMENTAL);
			errorReport.append(LINE_SEPARATOR);





				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("exception",exceptionLog);
				jsonObject.put("Brand",Build.BRAND);
				jsonObject.put("Device",Build.DEVICE);
				jsonObject.put("Model",Build.MODEL);
				jsonObject.put("Id",Build.ID);
				jsonObject.put("Product",Build.PRODUCT);
				jsonObject.put("SDK",Build.VERSION.SDK_INT);
				jsonObject.put("Release",Build.VERSION.RELEASE);
				jsonObject.put("Incremental",Build.VERSION.INCREMENTAL);


				jsonArray.put(jsonObject);





			Intent intent = new Intent(myContext, ErrorActivity.class);
			intent.putExtra("error", errorReport.toString());
			intent.putExtra("json", jsonArray.toString());
			myContext.startActivity(intent);
			myContext.finish();
			//  SendErrorMail(myContext,errorReport.toString() );

			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
		catch (JSONException e)
		{


		}
	}

	
	
}