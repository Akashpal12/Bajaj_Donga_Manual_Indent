package in.co.vibrant.bajajdongamanual.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import in.co.vibrant.bajajdongamanual.DB.DBHelper;
import in.co.vibrant.bajajdongamanual.R;
import in.co.vibrant.bajajdongamanual.model.UserDetailsModel;
import in.co.vibrant.bajajdongamanual.util.APIUrl;
import in.co.vibrant.bajajdongamanual.util.AlertDialogManager;
import in.co.vibrant.bajajdongamanual.util.GetDeviceImei;


public class Login extends AppCompatActivity {

    EditText Username,Password;
    Context context;
    DBHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context= Login.this;
        dbh=new DBHelper(context);
        Username=findViewById(R.id.login_username);
        Password=findViewById(R.id.login_password);

    }


    public void login(View v)
    {
        if(Username.getText().toString().length()==0)
        {
            new AlertDialogManager().RedDialog(context,"Please enter username");
        }
        else if(Password.getText().toString().length()==0)
        {
            new AlertDialogManager().RedDialog(context,"Please enter password");
        }
        else
        {
            new loginAction().execute(Username.getText().toString(),Password.getText().toString());
        }

    }


    private class loginAction extends AsyncTask<String, Integer, Void> {
        String message;
        private ProgressDialog dialog = new ProgressDialog(context);

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle(getString(R.string.app_name));
            //dialog.setIndeterminate(false);
            dialog.setMessage(getString(R.string.MSG_PLEASE_WAIT));
            /*dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);*/
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String imei=new GetDeviceImei(context).GetDeviceImeiNumber();
                SoapObject request1 = new SoapObject(APIUrl.NAMESPACE, APIUrl.method_ValidateUser);
                request1.addProperty("UserCode", params[0]);
                request1.addProperty("Password", params[1]);
                request1.addProperty("IMEI", imei);
                Log.d("", "doInBackground: " + request1);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request1);
                envelope.implicitTypes = true;
                // Web method call
                HttpTransportSE androidHttpTransport = new HttpTransportSE(APIUrl.BASE_URL, 200000);
                androidHttpTransport.debug = true;
                androidHttpTransport.call(APIUrl.SOAP_ACTION_ValidateUser, envelope);
                if (envelope.bodyIn instanceof SoapFault) {
                    SoapFault sf = (SoapFault) envelope.bodyIn;
                    message = sf.getMessage();
                } else {
                    SoapObject result = (SoapObject) envelope.bodyIn;
                    message = result.getPropertyAsString("ValidateUserResult").toString();
                }
            } catch (SecurityException e) {
                Log.e("Exception", e.getMessage());
                message = e.getMessage();
            }
            catch (Exception e) {
                Log.e("Exception", e.getMessage());
                message = e.getMessage();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(dialog.isShowing())
                dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(message);
                if(jsonObject.getString("API_STATUS").equalsIgnoreCase("OK"))
                {
                    JSONArray jsonArray=jsonObject.getJSONArray("DATA");
                    dbh.deleteUserDetailsModel();
                    if(jsonArray.length()>0)
                    {
                        JSONObject  object=jsonArray.getJSONObject(0);
                        UserDetailsModel userDetailsModel=new UserDetailsModel();
                        userDetailsModel.setFactoryCode(object.getString("U_FACTORY"));
                        userDetailsModel.setUserCode(object.getString("U_CODE"));
                        userDetailsModel.setUserName(object.getString("U_NAME"));
                        dbh.insertUserDetailsModel(userDetailsModel);
                        Intent intent=new Intent(context, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
                else
                {
                    new AlertDialogManager().RedDialog(context,jsonObject.getString("MSG"));
                }
            }
            catch(JSONException e)
            {
                //textView.setText("Error:"+e.toString());
                new AlertDialogManager().AlertPopUpFinish(context,"Error:"+e.toString());
            }
            catch(Exception e)
            {
                //textView.setText("Error:"+e.toString());
                new AlertDialogManager().AlertPopUpFinish(context,"Error:"+e.toString());
            }
        }
    }


}
