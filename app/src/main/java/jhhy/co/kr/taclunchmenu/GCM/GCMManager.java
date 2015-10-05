package jhhy.co.kr.taclunchmenu.GCM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.IOException;

import jhhy.co.kr.taclunchmenu.Const;
import jhhy.co.kr.taclunchmenu.MainActivity;

import static android.util.Log.d;

/**
 * Created by jhkim on 2015-04-25.
 */
public class GCMManager
{
    // GCM 설정값
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static GCMManager manager;

    String SENDER_ID = "652337868727";
    GoogleCloudMessaging gcm;
    String regid;



    public static GCMManager getInstance()
    {
        if(manager == null)
        {
            manager = new GCMManager();
        }
        return manager;
    }

    public void registGCM(Activity contActivity)
    {
        // GCM 작업
        if (checkPlayServices(contActivity))
        {
            gcm = GoogleCloudMessaging.getInstance(contActivity);
            regid = getRegistrationId(contActivity);
            d("HWI DEBUG", "푸시 등록 아이디 확인 regid : " + regid);
            if (regid.isEmpty())
            {
                registerInBackground(contActivity);
            }
            else
            {
                d("HWI DEBUG", "푸시 등록 아이디가 이미 있으므로 구글 서버에 요청하지 않습니다.  regid : " + regid);
            }
        } else
        {
            d("HWI DEBUG", "No valid Google Play Services APK found. 구글 플레이서비스 APK 찾을 수 없음");
        }
    }

    // GCM regid 가져오기
    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty())
        {
            d("HWI DEBUG", "푸시 등록 regId가 비어있음");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            d("HWI DEBUG", "앱 버전이 변경됨");
            return "";
        }
        return registrationId;
    }

    // 구글 플레이서비스 사용 가능 여부 확인
    private boolean checkPlayServices(Activity contActivity)
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(contActivity);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, contActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else
            {

                d("HWI DEBUG", "구글 플레이 서비스를 사용할 수 없는 단말입니다.");
                contActivity.finish();
            }
            return false;
        }
        return true;
    }

    // GCM 키 백그라운드에서 가져오기 및 3rd party Server에 저장
    private void registerInBackground(final Activity contActivity)
    {
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(contActivity);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    d("HWI DEBUG", "regid 얻음 : " + regid);

                    storeRegistrationId(contActivity, regid);
                } catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object message)
            {
                d("HWI DEBUG", "regId 얻기 완료~ message : " + message);

                AsyncHttpClient client = new AsyncHttpClient();

                d("HWI DEBUG", "얻은 regid 서버 저장값 : " + regid);

                String android_id = Settings.Secure.getString(contActivity.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                d("HWI DEBUG", "얻은 android_id 값 : "+android_id);
                RequestParams params = new RequestParams();
                params.put("regid", regid);
                params.put("salt", "whiteday910");
                params.put("android_id", android_id);
                client.post(Const.URL_SET_GCM_REG_ID, params, new AsyncHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes)
                    {
                        d("HWI DEBUG", " GCM 서버 저장 성공 : " + new String(bytes));
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable)
                    {
                        d("HWI DEBUG", "GCM 서버 저장 실패 onFailure : " + new String(bytes));
                    }
                });

                super.onPostExecute(message);
            }
        }.execute(null, null, null);
    }



    // regid 를 내부 저장소에 앱 버전과 함께 저장
    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        d("HWI DEBUG", " regid 를 버전과 함께 저장합니다 : appVersion : " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    // 내부 저장소에 GCM regid 및 앱 버전 저장하기 위한 객체 가져오기
    private SharedPreferences getGCMPreferences(Context context)
    {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    // 앱 버전 확인
    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}



