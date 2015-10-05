package jhhy.co.kr.taclunchmenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import static android.util.Log.d;


public class NavigationDrawerFragment extends Fragment
{
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment()
    {
        d("HWI DEBUG", "NavigationDrawerFragment 생성자 호출됨");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null)
        {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        selectItem(mCurrentSelectedPosition);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        LinearLayout drawerContainer = (LinearLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) drawerContainer.findViewById(R.id.drawerListView);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(new ArrayAdapter<String>
        (
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{"오늘의 메뉴", "주간 메뉴", "설정"}
        ));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return drawerContainer;
    }

    public boolean isDrawerOpen()
    {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout)
    {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
        {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    private void selectItem(int position)
    {
        d("HWI DEBUG", "네비드로어프래그먼트 selectItem 디버그01 position : " + position);
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null)
        {
            d("HWI DEBUG", "네비드로어프래그먼트 selectItem 디버그02 mDrawerListView : " + mDrawerListView);
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null)
        {
            d("HWI DEBUG", "네비드로어프래그먼트 selectItem 디버그03 mDrawerLayout : " + mDrawerLayout);
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null)
        {
            d("HWI DEBUG", "네비드로어프래그먼트 selectItem 디버그04 mCallbacks : " + mCallbacks);
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

    }

    @Override
    public void onAttach(Activity activity)
    {
        d("HWI DEBUG", "네비드로어프래그먼트 onAttach 디버그01 activity : " + activity);
        super.onAttach(activity);
        try
        {
            mCallbacks = (NavigationDrawerCallbacks) activity;
            d("HWI DEBUG", "네비드로어프래그먼트 onAttach 디버그02 activity : " + activity);
        } catch (ClassCastException e)
        {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach()
    {
        Log.d("HWI DEBUG", "네비게이션드로어프래그먼트 onDetach() 호출");
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        d("HWI DEBUG", "네비드로어프래그먼트 onSaveInstanceState 디버그01 outState : " + outState);
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        d("HWI DEBUG", "네비드로어프래그먼트 onConfigurationChanged 디버그01 newConfig : " + newConfig);
        super.onConfigurationChanged(newConfig);

    }




    public static interface NavigationDrawerCallbacks
    {
        void onNavigationDrawerItemSelected(int position);
    }
}
