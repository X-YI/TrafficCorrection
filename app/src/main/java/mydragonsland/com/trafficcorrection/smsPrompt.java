package mydragonsland.com.trafficcorrection;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;

public class smsPrompt extends Activity {

    String TAG = "SMS_PROMPT";

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().
        setContentView(R.layout.main);
    }

    public void sendMsg(View view) {
        send1("10086", "服务");
    }

    private void send1(String phone, String message) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, smsPrompt.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, pi, null);
    }

    public void onResume() {
        super.onResume();
        setContentView(R.layout.main);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("mySMS");

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String s = null;
            for (Object o : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) o);

                //strip flag
                String message = sms.getMessageBody();
                s += message;
            }

            String regex = "剩余(\\d+)KB";
            Pattern p = Pattern.compile(regex);
            String[] words = p.split(s);
            String regex2 = ",";
            Pattern p2 = Pattern.compile(regex2);
            String[] words2 = p2.split(s);

            String wholeWord = null;
            for (String word : words2) {
                wholeWord += word + "\r\n";
            }

            String s2 = s;
            for (String word : words) {
                Log.d("Regex", word);
                if (s2.contains(word)) {
                    s2 = s2.replace(word, "");
                }
            }

            Log.d("Regex", s2);
            s2 = s2.replace("剩余", "");
            String[] traffics = s2.split("KB");

            int sum = 0;
            for (String traffic : traffics) {
                int i = Integer.parseInt(traffic);
                sum += i;
            }
            // Convert TO MB.
            sum /= 1024;

            TextView tx = (TextView) findViewById(R.id.TextBox);

            String finalWord = null;
            String trafficSummary = "可用流量总计:" + sum + "MB";
            finalWord += trafficSummary + "\r\n";
            finalWord += s2 + "\r\n";
            finalWord += s + "\r\n";
            finalWord += wholeWord + "\r\n";
            tx.setText(finalWord);
        } else
            Log.i(TAG, "smsActivity : NULL SMS bundle");
    }

}

