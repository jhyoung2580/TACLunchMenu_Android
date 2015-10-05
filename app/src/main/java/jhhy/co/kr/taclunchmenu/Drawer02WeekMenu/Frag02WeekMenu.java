package jhhy.co.kr.taclunchmenu.Drawer02WeekMenu;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

import jhhy.co.kr.taclunchmenu.GlobalFunction;
import jhhy.co.kr.taclunchmenu.MenuListFragment;
import jhhy.co.kr.taclunchmenu.MenuManager;
import jhhy.co.kr.taclunchmenu.R;

import static android.util.Log.d;

/**
 * Created by jhkim on 2015-05-03.
 */
public class Frag02WeekMenu extends android.support.v4.app.Fragment {
    private static final String[] CONTENT = new String[]{"점심메뉴", "저녁메뉴"};
    private static final int[] ICONS = new int[]{
            R.drawable.sun,
            R.drawable.moon
    };

    ArrayList<Integer> arrayOfToggleBtn;

    public static Frag02WeekMenu newInstance() {

        return new Frag02WeekMenu();
    }

    private FragmentActivity myContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.fragment_frag02_week_menu, container, false);

        if (arrayOfToggleBtn == null) {
            arrayOfToggleBtn = new ArrayList<Integer>();
            arrayOfToggleBtn.add(R.id.day01MondayRadio);
            arrayOfToggleBtn.add(R.id.day02TuesdayRadio);
            arrayOfToggleBtn.add(R.id.day03WednesdayRadio);
            arrayOfToggleBtn.add(R.id.day04ThursdayRadio);
            arrayOfToggleBtn.add(R.id.day05FridayRadio);
            arrayOfToggleBtn.add(R.id.day06SaturdayRadio);
            arrayOfToggleBtn.add(R.id.day07SundayRadio);
        }

        // ViewPager 셋팅
        final FragmentStatePagerAdapter adapter = new MenuListAdapter(myContext.getSupportFragmentManager(), 0);

        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) v.findViewById(R.id.indicator);
        indicator.setViewPager(pager);


        final View.OnClickListener toggleBtnListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View clickedBtn) {
                for (int i = 0; i < arrayOfToggleBtn.size(); i++) {
                    int idOfOneToggleBtn = arrayOfToggleBtn.get(i);
                    ((ToggleButton) v.findViewById(idOfOneToggleBtn)).setChecked(false);
                }
                ((ToggleButton) clickedBtn).setChecked(true);

                int indexOfWeek = arrayOfToggleBtn.indexOf(clickedBtn.getId());
                ((MenuListAdapter) adapter).currentDayOfWeek = indexOfWeek;
                Log.d("HWI DEBUG", "데이터 셋 체인지 확인 01");
                adapter.notifyDataSetChanged();
            }
        };

        for (int i = 0; i < arrayOfToggleBtn.size(); i++)
        {
            int idOfOneToggleBtn = arrayOfToggleBtn.get(i);
            ToggleButton oneBtn = (ToggleButton) v.findViewById(idOfOneToggleBtn);
            oneBtn.setOnClickListener(toggleBtnListener);

            if(MenuManager.getInstance().thisWeekData.size() > 0)
            {
                String oneDateString = MenuManager.getInstance().thisWeekData.get(i).todayTitleString;
                Log.d("HWI DEBUG", "텍스트 변경 실행 확인 : " + oneDateString);
                oneBtn.setTextOn(oneDateString);
                oneBtn.setTextOff(oneDateString);
                oneBtn.setText(oneDateString);
            }

        }

        adapter.notifyDataSetChanged();
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    // ViewPager 어댑터
    class MenuListAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

        public int currentDayOfWeek;

        public MenuListAdapter(FragmentManager fm, int currentDayOfWeek) {
            super(fm);
            this.currentDayOfWeek = currentDayOfWeek;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            d("HWI DEBUG", "getItem 호출 확인 포지션: " + position);
            if (position == 0) {
                return MenuListFragment.newInstance(GlobalFunction.TYPE_MENU_WEEK, currentDayOfWeek, 0);
            } else {
                return MenuListFragment.newInstance(GlobalFunction.TYPE_MENU_WEEK, currentDayOfWeek, 1);
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
