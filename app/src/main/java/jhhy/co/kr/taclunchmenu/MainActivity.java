package jhhy.co.kr.taclunchmenu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import jhhy.co.kr.taclunchmenu.Drawer01TodayMenu.Frag01TodayMenu;
import jhhy.co.kr.taclunchmenu.Drawer02WeekMenu.Frag02WeekMenu;
import jhhy.co.kr.taclunchmenu.Drawer03Setting.Frag03Setting;
import jhhy.co.kr.taclunchmenu.GCM.GCMManager;

import static android.util.Log.d;

public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks , Frag01TodayMenu.OnFragmentInteractionListener
{
    private NavigationDrawerFragment mNavigationDrawerFragment;

    DrawerLayout drawerLayout;

    private Frag01TodayMenu.OnFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        d("HWI DEBUG", "메인액티비티 디버그01 onCreate");
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp( R.id.navigation_drawer, drawerLayout);

        // GCM 등록
        GCMManager.getInstance().registGCM(MainActivity.this);

        // 첫 앱 실행시 드로어 닫음
        drawerLayout.closeDrawer(Gravity.LEFT);

    }


    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        d("HWI DEBUG", "메인 액티비티에서 리스트 선택되었을 때 포지션  : "+position);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position)
        {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.container, ((Fragment) Frag01TodayMenu.newInstance())).commit();
                break;
            case 1:

                GlobalFunction.doWorkIfOnlineState(MainActivity.this, new GlobalFunction.OnlineNetworkListener()
                {
                    @Override
                    public void doWork()
                    {
                        d("HWI DEBUG", "온라인 상태로 주간 메뉴를 인터넷에서 가져옵니다.");
                        final ProgressDialog progress = ProgressDialog.show(MainActivity.this, "주간 메뉴 가져오는 중", "잠시만 기다려 주세요", true);

                        MenuManager.getInstance().getWeekDataFromServer(new MenuManager.OnDataReceiveListener()
                        {
                            @Override
                            public void OnDataReceived()
                            {
                                d("HWI DEBUG", "주간 메뉴를 모두 가져와서 화면을 전환합니다.");
                                fragmentManager.beginTransaction().replace(R.id.container, ((Fragment) Frag02WeekMenu.newInstance())).commit();
                                progress.dismiss();
                            }
                        });
                    }
                });
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.container, ((Fragment) Frag03Setting.newInstance())).commit();
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {
        Log.d("HWI DEBUG", "메인액티비티 onFragmentInteraction 에서 uri: "+uri);
    }

    // 메뉴 키를 눌렀을 때 행동 정의 -> 네비게이션 드로어 On / off
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            if(drawerLayout.isDrawerOpen(Gravity.LEFT))
            {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
            else
            {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
        {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return ;
        }

        GlobalFunction.showOneBtnDialog(MainActivity.this,"앱 종료 확인","앱을 종료하시겠습니까?",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
    }
}
