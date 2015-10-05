package jhhy.co.kr.taclunchmenu.GCM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import jhhy.co.kr.taclunchmenu.MainActivity;
import jhhy.co.kr.taclunchmenu.R;


public class ActivityForGCM extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_for_gcm);
        TextView titleTextGCMAlert = (TextView)findViewById(R.id.titleTextGCMAlert);
        TextView messageTextGCMAlert = (TextView)findViewById(R.id.messageTextGCMAlert);
        Bundle bd = getIntent().getExtras();
        titleTextGCMAlert.setText(bd.getString("title"));
        messageTextGCMAlert.setText(bd.getString("message"));

        Button btnGCMAlert = (Button)findViewById(R.id.btnGCMAlert);
        btnGCMAlert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ActivityForGCM.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

}
