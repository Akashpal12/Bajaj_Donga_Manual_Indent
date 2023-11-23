package in.co.vibrant.bajajdongamanual.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import in.co.vibrant.bajajdongamanual.DB.DBHelper;
import in.co.vibrant.bajajdongamanual.R;
import in.co.vibrant.bajajdongamanual.model.UserDetailsModel;
import in.co.vibrant.bajajdongamanual.model.VarietyModel;
import in.co.vibrant.bajajdongamanual.util.APIUrl;
import in.co.vibrant.bajajdongamanual.util.AlertDialogManager;
import in.co.vibrant.bajajdongamanual.util.GetDeviceImei;


public class MainActivity extends AppCompatActivity {

    DBHelper dbh;
    Context context;
    EditText Gross_Slip_Number,Token_Number,Grower_Village_Code,Grower_Name,Grower_Father_Name,Grower_Village_Name,Variety_Code_Name,
            Variety_Category_Code_Name;
    List<UserDetailsModel> userDetailsModelList;
    List<VarietyModel> varietyModelList;
    Spinner variety;
    TextView v_msg;

    TextInputLayout variety_code_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        context=this;
        userDetailsModelList=new ArrayList<>();
        dbh=new DBHelper(context);
        userDetailsModelList=dbh.getUserDetailsModel();
        varietyModelList=new ArrayList<>();
        Gross_Slip_Number= findViewById(R.id.Gross_Slip_Number);
        Token_Number= findViewById(R.id.Token_Number);
        Grower_Village_Code= findViewById(R.id.Grower_Village_Code);
        Grower_Name= findViewById(R.id.Grower_Name);
        Grower_Father_Name= findViewById(R.id.Grower_Father_Name);
        Grower_Village_Name= findViewById(R.id.Grower_Village_Name);
        Variety_Code_Name= findViewById(R.id.Variety_Code_Name);
        Variety_Category_Code_Name= findViewById(R.id.Variety_Category_Code_Name);
        variety= findViewById(R.id.variety);
        v_msg= findViewById(R.id.v_msg);
        variety_code_layout= findViewById(R.id.variety_code_layout);

        Gross_Slip_Number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    if(Gross_Slip_Number.getText().toString().length()>0)
                    {
                        new verifyGrossSlipNo().execute(Gross_Slip_Number.getText().toString());
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        closeApplication();
    }


    public void closeApplication() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage("Are you sure you want to close app ?");
        alertDialog.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void exitApp(View v)
    {
        closeApplication();
    }


    public void changeVariety(View v)
    {
        variety_code_layout.setVisibility(View.VISIBLE);
    }

    public void saveData(View v)
    {
        if(Gross_Slip_Number.getText().toString().length()==0)
        {
            new AlertDialogManager().RedDialog(context,"Enter Gross Slip Number");
        }
        else if(Token_Number.getText().toString().length()==0)
        {
            new AlertDialogManager().RedDialog(context,"Enter Gross Token Number");
        }
        else if(variety.getSelectedItemPosition()==0)
        {
            new AlertDialogManager().RedDialog(context,"Select Variety Code");
        }
        else
        {
            new saveData().execute(Token_Number.getText().toString(),Gross_Slip_Number.getText().toString(),
                    varietyModelList.get(variety.getSelectedItemPosition()-1).getVarietyCategoryCode(),
                    varietyModelList.get(variety.getSelectedItemPosition()-1).getVarietyCode());
        }
    }


    private class verifyGrossSlipNo extends AsyncTask<String, Integer, Void> {
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
                SoapObject request1 = new SoapObject(APIUrl.NAMESPACE, APIUrl.method_ValidateCardNumber);
                request1.addProperty("Factory", userDetailsModelList.get(0).getFactoryCode());
                request1.addProperty("IndentNo", params[0]);
                request1.addProperty("UserCode", userDetailsModelList.get(0).getUserCode());
                request1.addProperty("IMEI", imei);
                Log.d("", "doInBackground: " + request1);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request1);
                envelope.implicitTypes = true;
                // Web method call
                HttpTransportSE androidHttpTransport = new HttpTransportSE(APIUrl.BASE_URL, 200000);
                androidHttpTransport.debug = true;
                androidHttpTransport.call(APIUrl.SOAP_ACTION_ValidateCardNumber, envelope);
                if (envelope.bodyIn instanceof SoapFault) {
                    SoapFault sf = (SoapFault) envelope.bodyIn;
                    message = sf.getMessage();
                } else {
                    SoapObject result = (SoapObject) envelope.bodyIn;
                    message = result.getPropertyAsString("ValidateCardNumberResult").toString();
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
                    JSONArray varietyJsonArray=jsonObject.getJSONArray("VARIETY");
                    JSONArray jsonArray=jsonObject.getJSONArray("CARDDATA");
                    if(jsonArray.length()>0)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);
                        Grower_Village_Code.setText(jsonObject1.getString("V_CODE")+" / "+jsonObject1.getString("G_CODE"));
                        Grower_Village_Name.setText(jsonObject1.getString("V_NAME"));
                        Grower_Name.setText(jsonObject1.getString("G_NAME"));
                        Grower_Father_Name.setText(jsonObject1.getString("G_FATHER"));
                        Variety_Code_Name.setText(jsonObject1.getString("VR_CODE")+"/"+jsonObject1.getString("VR_NAME"));
                        Variety_Category_Code_Name.setText(jsonObject1.getString("G_CANECATEGORY"));
                        Token_Number.setText(jsonObject1.getString("G_TOKENNO"));

                        if(varietyModelList.size()>0)
                            varietyModelList.clear();
                        int varietyIndex=0;
                        if(varietyJsonArray.length()>0)
                        {
                            ArrayList<String> data=new ArrayList<String>();
                            data.add("");
                            for(int i=0;i<varietyJsonArray.length();i++)
                            {
                                JSONObject object=varietyJsonArray.getJSONObject(i);
                                VarietyModel varietyModel=new VarietyModel();
                                varietyModel.setVarietyCode(object.getString("VR_CODE"));
                                varietyModel.setVarietyName(object.getString("VR_NAME"));
                                data.add(object.getString("VR_NAME"));
                                varietyModel.setVarietyCategoryCode(object.getString("VR_CANE_TYPE"));
                                if(object.getString("VR_CODE").equalsIgnoreCase(jsonObject1.getString("VR_CODE")))
                                {
                                    varietyIndex=i+1;
                                }
                                varietyModelList.add(varietyModel);
                            }
                            ArrayAdapter<String> adaptersupply = new ArrayAdapter<String>(context,
                                    R.layout.list_item, data);
                            variety.setAdapter(adaptersupply);
                            variety.setSelection(varietyIndex);
                            variety.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if(i>0)
                                    {
                                        v_msg.setText("Variety category code is "+varietyModelList.get(i-1).getVarietyCategoryCode());
                                    }
                                    else
                                    {
                                        v_msg.setText("");
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }
                    }

                }
                else
                {
                    Gross_Slip_Number.setText("");
                    Gross_Slip_Number.setError(jsonObject.getString("MSG"));
                    Gross_Slip_Number.requestFocus();
                    Grower_Village_Code.setText("");
                    Grower_Village_Name.setText("");
                    Grower_Name.setText("");
                    Grower_Father_Name.setText("");
                    Variety_Code_Name.setText("");
                    Variety_Category_Code_Name.setText("");
                    Token_Number.setText("");
                    //new AlertDialogManager().RedDialog(context,jsonObject.getString("MSG"));
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


    private class saveData extends AsyncTask<String, Integer, Void> {
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
                SoapObject request1 = new SoapObject(APIUrl.NAMESPACE, APIUrl.method_UpdateDonga);
                request1.addProperty("Factory", userDetailsModelList.get(0).getFactoryCode());
                request1.addProperty("TokenNumber", params[0]);
                request1.addProperty("G_INDENTNO", params[1]);
                request1.addProperty("CATEG", params[2]);
                request1.addProperty("VAR", params[3]);
                request1.addProperty("UserCode", userDetailsModelList.get(0).getUserCode());
                request1.addProperty("IMEI", imei);
                Log.d("", "doInBackground: " + request1);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request1);
                envelope.implicitTypes = true;
                // Web method call
                HttpTransportSE androidHttpTransport = new HttpTransportSE(APIUrl.BASE_URL, 200000);
                androidHttpTransport.debug = true;
                androidHttpTransport.call(APIUrl.SOAP_ACTION_UpdateDonga, envelope);
                if (envelope.bodyIn instanceof SoapFault) {
                    SoapFault sf = (SoapFault) envelope.bodyIn;
                    message = sf.getMessage();
                } else {
                    SoapObject result = (SoapObject) envelope.bodyIn;
                    message = result.getPropertyAsString("UpdateDongaResult").toString();
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
                    Intent intent=new Intent(context,MainActivity.class);
                    new AlertDialogManager().AlertPopUpFinishWithIntent(context,jsonObject.getString("MSG"),intent);
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
