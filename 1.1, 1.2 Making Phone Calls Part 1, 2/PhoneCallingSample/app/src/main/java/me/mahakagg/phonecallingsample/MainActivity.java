package me.mahakagg.phonecallingsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private TelephonyManager telephonyManager;
    private MyPhoneCallListener mListener;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (isTelephonyEnabled()) {
            Log.d(TAG, getResources().getString(R.string.telephony_enabled));
            checkForPhonePermission();
            mListener = new MyPhoneCallListener();
            telephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        else {
            Toast.makeText(this, getString(R.string.telephony_not_enabled), Toast.LENGTH_LONG).show();
            Log.d(TAG, getResources().getString(R.string.telephony_not_enabled));
            disableCallButton();
        }
    }

    public void callNumber(View view) {
        EditText editText = findViewById(R.id.editText_main);
        String phoneNumber = String.format("tel: %s", editText.getText().toString());
        Log.d(TAG, getResources().getString(R.string.dial_number) + phoneNumber);
        Toast.makeText(this, getResources().getString(R.string.dial_number) + phoneNumber, Toast.LENGTH_LONG).show();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            checkForPhonePermission();
            startActivity(callIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_CALL Intent.");
        }
    }

    private boolean isTelephonyEnabled(){
        if (telephonyManager != null) {
            return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        }
        return false;
    }

    private void disableCallButton() {
        Toast.makeText(this, getString(R.string.phone_disabled), Toast.LENGTH_LONG).show();
        ImageButton callButton = findViewById(R.id.phone_icon);
        callButton.setVisibility(View.INVISIBLE);
        if (isTelephonyEnabled()) {
            Button retryButton = findViewById(R.id.button_retry);
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    private void enableCallButton() {
        ImageButton callButton = findViewById(R.id.phone_icon);
        callButton.setVisibility(View.VISIBLE);
    }

    public void retryApp(View view) {
        enableCallButton();
        // restart app
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(intent);
    }

    private void checkForPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION NOT GRANTED!");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            // Permission already granted
            enableCallButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if permission is granted or not for the request.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.CALL_PHONE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    Log.d(TAG, "Failure to obtain permission!");
                    Toast.makeText(this, "Failure to obtain permission!", Toast.LENGTH_LONG).show();
                    disableCallButton();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTelephonyEnabled()) {
            telephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    // internal class
    private class MyPhoneCallListener extends PhoneStateListener{
        private boolean returningFromOffHook = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            String message = "Phone Status: ";
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    message = message + "RINGING, number: " + incomingNumber;
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    message = message + "OFFHOOK";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    returningFromOffHook = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    message = message + "IDLE";
                    Toast.makeText(MainActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    if (returningFromOffHook) {
                        // No need to do anything because api > 19 (kitkat)
                    }
                    break;
                default:
                    message = message + "Phone off";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
            }
        }
    }
}
