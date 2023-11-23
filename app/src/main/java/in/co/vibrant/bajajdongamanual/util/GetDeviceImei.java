package in.co.vibrant.bajajdongamanual.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class GetDeviceImei {

    Context context;

    public GetDeviceImei(Context context) {
        this.context = context;
    }

    public String GetDeviceImeiNumber() {
        try {
            String imei;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = tm.getImei();
            } else {
                imei = tm.getDeviceId();
            }
            if (imei != null && !imei.isEmpty()) {
            }
            //return "96a66de3c76dd974";
            //if(imei.equalsIgnoreCase("96a66de3c76dd974"))
            if(imei.equalsIgnoreCase("867523037792028"))
            {
                return "123";
            }
            else
            {
                return imei;
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    public String GetSimOperator() {
        try {
            String imei;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = tm.getNetworkOperatorName();
            } else {
                imei = tm.getNetworkOperatorName();
            }
            if (imei != null && !imei.isEmpty()) {

            }
            return imei;
        } catch (Exception e) {
            return "Error";
        }
    }

    public boolean isAirplaneModeOn() {

        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 1;

    }

    public Boolean isMobileDataEnabled(){
        Object connectivityService = context.getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean)m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int checkUplinkStatus()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
        return upSpeed;
    }

    public int checkDownlinkStatus()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
        return downSpeed;
    }

}
