package jhhy.co.kr.taclunchmenu.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import jhhy.co.kr.taclunchmenu.GlobalFunction;

import static android.util.Log.d;

/**
 * Created by jhkim on 2015-04-16.
 */
public class GCMReceiver extends WakefulBroadcastReceiver
{
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;

        if (GlobalFunction.loadIsPushReceiveAllow(context))
        {

            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty())
            {

                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
                {

                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED.equals(messageType))
                {


                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
                {
                    // 만약 사용자가 푸시 받기를 체크(On) 한 상태라면 띄운다.
                    if (GlobalFunction.loadIsPushReceiveAllow(context))
                    {


                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(1000);

                        String title = extras.getString("title");
                        String message = extras.getString("message");

                        d("HWI DEBUG", "노티피케이션 Received Data : " + extras.toString());

                        showDialogPopUp(title,message);


                        PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
                        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                        wl.acquire(15000);
                        GCMReceiver.completeWakefulIntent(intent);
                    }

                }
            }
        }
    }



    private void showDialogPopUp(String title, String msg)
    {
        try
        {
            Intent intent = new Intent(context,ActivityForGCM.class);
            intent.putExtra("title",title);
            intent.putExtra("message",msg);
            PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            d("HWI DEBUG", "펜딩인텐트로 팝업창 요청 pintent : "+pintent);
            pintent.send();
        }
        catch (Exception e)
        {
            d("HWI DEBUG", "GCM 팝업 중 이상 발생 에러메시지 : " + e.getMessage());
        }

    }
}
