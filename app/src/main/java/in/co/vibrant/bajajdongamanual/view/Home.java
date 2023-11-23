package in.co.vibrant.bajajdongamanual.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.vibrant.bajajdongamanual.DB.DBHelper;
import in.co.vibrant.bajajdongamanual.R;

/**
 * Created by Shobhit on 11-08-2017.
 */

public class Home extends AppCompatActivity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.GET_TASKS,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
    };
    boolean allgranted=false;
    String FirbaseRegistrationId;
    TextView textView;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    Context context;
    DBHelper dbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        context= Home.this;
        dbh = new DBHelper(context);
        db = new DBHelper(context).getWritableDatabase();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        textView=(TextView)findViewById(R.id.textView);
        checkPermission();

    }

    private void checkPermission()
    {
        textView.setText("Check Permission");
        ActivityCompat.requestPermissions(Home.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[4])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[5])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[6])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[7])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[8])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permissionsRequired[9])
            ) {
                //txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle(getString(R.string.MSG_NEED_PERMISSION));
                builder.setMessage(getString(R.string.MSG_NEED_PERMISSION_REQUEST));
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.BTN_YES), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Home.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(getString(R.string.BTN_LATER), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                builder.show();
            } else {
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(Home.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                textView.setText("All permission granted");
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dbh.onUpgrade(db,1,2);
                Intent intent=new Intent(Home.this,Login.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }


}
