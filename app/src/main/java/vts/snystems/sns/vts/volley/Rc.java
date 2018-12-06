package vts.snystems.sns.vts.volley;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;
import java.util.Map;

import vts.snystems.sns.vts.classes.MyApplication;


public class Rc
{

    public static void withotProgress(final VolleyCallback callback,
                                          final VolleyErrorCallback errorLog,
                                          final String url,
                                          final String[] parameters,
                                          Context context,
                                          final String status)
    {


        //progressDialog.setCancellable(true);



        try
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {


                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            errorLog.onError(error);

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String[] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };

            MyApplication.requestQueue.getCache().clear();
            stringRequest.setRetryPolicy(MyApplication.retryPolicy);
            MyApplication.requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void withParamsProgress(final VolleyCallback callback,
                                          final VolleyErrorCallback errorLog,
                                          final String url,
                                          final String[] parameters,
                                          Context context,
                                          final String status)
    {

        final KProgressHUD progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f);
        //progressDialog.setCancellable(true);

        if(status.equals("first"))
        {
            progressDialog.show();
        }



        try
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            if(status.equals("first"))
                            {
                                progressDialog.dismiss();
                            }
                            else
                            {
                                progressDialog.dismiss();
                            }

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {


                            if(status.equals("first"))
                            {
                                progressDialog.dismiss();
                            }
                            else
                            {
                                progressDialog.dismiss();
                            }
                            errorLog.onError(error);

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String[] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };

            MyApplication.requestQueue.getCache().clear();
            stringRequest.setRetryPolicy(MyApplication.retryPolicy);
            MyApplication.requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //without params
    public static void withoutParams(final VolleyCallback callback,
                                  final VolleyErrorCallback errorLog,
                                  final String url)
    {

        try
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {


                            errorLog.onError(error);

                        }
                    });

            MyApplication.requestQueue.getCache().clear();
            stringRequest.setRetryPolicy(MyApplication.retryPolicy);
            MyApplication.requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
