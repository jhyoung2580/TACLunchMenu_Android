package jhhy.co.kr.taclunchmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.util.Log.d;

/**
 * Created by jhkim on 2015-04-22.
 */
public class GlobalFunction
{
    public static final int TYPE_MENU_TODAY = 0;
    public static final int TYPE_MENU_WEEK = 1;

    private static final String KEY_PREF = "HWI";
    private static final String KEY_IS_PUSH = "PUSH";

    public interface OnlineNetworkListener
    {
        public void doWork();
    }

    // 온라인 여부 검사
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // 단일 버튼 얼럿다이얼로그 생성
    public static void showOneBtnDialog(Context context,String title, String message, DialogInterface.OnClickListener listener)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if(title != null)
        alertDialog.setTitle(title);

        if(message != null)
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(android.R.string.ok),listener);
        alertDialog.show();
    }

    // 푸시메시지 수신 허용 여부 저장
    public static void saveIsPushReceive(Context context,boolean isPushReceive)
    {
        SharedPreferences pref = context.getSharedPreferences(KEY_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean(KEY_IS_PUSH,isPushReceive);
        edt.commit();
    }

    // 푸시메시지 수신 허용 여부 로드
    public static boolean loadIsPushReceiveAllow(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(KEY_PREF, context.MODE_PRIVATE);
        return pref.getBoolean(KEY_IS_PUSH,true);
    }

    public static void doWorkIfOnlineState(final Activity activity, OnlineNetworkListener listener)
    {
        if (!isOnline(activity))
        {

            d("HWI DEBUG", "온라인 상태가 아닙니다.");
            GlobalFunction.showOneBtnDialog(activity, "인터넷 연결 에러", "인터넷 연결을 확인 후 앱을 재시작 해 주세요", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                    activity.finish();
                }
            });
        }
        else
        {
            listener.doWork();
        }
    }


}
