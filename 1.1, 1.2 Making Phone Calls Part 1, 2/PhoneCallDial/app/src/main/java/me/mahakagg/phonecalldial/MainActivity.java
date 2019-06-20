package me.mahakagg.phonecalldial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void dialNumber(View view) {
        TextView textView = findViewById(R.id.number_to_call);
        String phoneNumber = String.format("tel: %s", textView.getText().toString());
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(phoneNumber));
        if (dialIntent.resolveActivity(getPackageManager()) != null){
            startActivity(dialIntent);
        }
        else{
            Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
        }
    }
}
