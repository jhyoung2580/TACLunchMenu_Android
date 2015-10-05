package jhhy.co.kr.taclunchmenu.Drawer01TodayMenu;


import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import jhhy.co.kr.taclunchmenu.GlobalFunction;
import jhhy.co.kr.taclunchmenu.MenuListFragment;
import jhhy.co.kr.taclunchmenu.MenuManager;
import jhhy.co.kr.taclunchmenu.R;

import static android.util.Log.d;

public class Frag01TodayMenu extends android.support.v4.app.Fragment
{
    // PageIndicator 설정값
    private static final String[] CONTENT = new String[]{"점심메뉴", "저녁메뉴"};
    private static final int[] ICONS = new int[]{
            R.drawable.sun,
            R.drawable.moon
    };

    private FragmentActivity myContext;

    public static Frag01TodayMenu newInstance()
    {
        return new Frag01TodayMenu();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.fragment_frag01_today_menu, container, false);


        // ViewPager 셋팅
        final FragmentPagerAdapter adapter = new MenuListAdapter(myContext.getSupportFragmentManager());
        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        d("HWI DEBUG", "뷰페이져 어댑터 셋팅 완료");

        TabPageIndicator indicator = (TabPageIndicator) v.findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        d("HWI DEBUG", "TabPageIndicator 에 뷰페이져 셋팅 완료");

        Log.d("HWI DEBUG", "Frag01TodayMenu : adapter :" + adapter);

        // 인터넷 접속 가능 상태인지 검사
        GlobalFunction.doWorkIfOnlineState(getActivity(), new GlobalFunction.OnlineNetworkListener() {
            @Override
            public void doWork() {
                d("HWI DEBUG", "온라인 상태로 메뉴를 인터넷에서 가져옵니다.");
                final ProgressDialog progress = ProgressDialog.show(myContext, "메뉴 가져오는 중", "잠시만 기다려 주세요", true);
                // 웰스토리,아워홈 메뉴 파싱
                MenuManager.getInstance().getTodayDataFromServer(new MenuManager.OnDataReceiveListener() {
                    @Override
                    public void OnDataReceived() {
                        d("HWI DEBUG", "서버에서 데이터를 받은 후 콜백함수가 실행됩니다. 어댑터에 변화를 알립니다.");
                        adapter.notifyDataSetChanged();
                        progress.dismiss();
                    }
                });
            }
        });




        //광고 호출하기
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return v;
    }


    @Override
    public void onAttach(Activity activity)
    {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }



    public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(Uri uri);
    }


    // ViewPager 어댑터
    class MenuListAdapter extends FragmentPagerAdapter implements IconPagerAdapter
    {
        public MenuListAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            d("HWI DEBUG", "getItem 호출 확인 포지션: " + position);
            if (position == 0)
            {
                return MenuListFragment.newInstance(GlobalFunction.TYPE_MENU_TODAY,0,0);
            } else
            {
                return MenuListFragment.newInstance(GlobalFunction.TYPE_MENU_TODAY,0,1);
            }
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getIconResId(int index)
        {
            return ICONS[index];
        }

        @Override
        public int getCount()
        {
            return CONTENT.length;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }
    }
}
