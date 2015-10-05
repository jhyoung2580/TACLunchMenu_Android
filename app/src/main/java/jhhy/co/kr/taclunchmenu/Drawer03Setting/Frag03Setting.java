package jhhy.co.kr.taclunchmenu.Drawer03Setting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import jhhy.co.kr.taclunchmenu.Const;
import jhhy.co.kr.taclunchmenu.GlobalFunction;
import jhhy.co.kr.taclunchmenu.R;

/**
 * Created by sk01 on 2015-05-04.
 */
public class Frag03Setting extends android.support.v4.app.Fragment{

    FragmentActivity myContext;
    int countOfEasterEggBtnClick =0;

    public static Frag03Setting newInstance() {
        return new Frag03Setting();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.fragment_frag03_setting, container, false);
        // 체크박스 처리
        CheckBox chkPush = (CheckBox) v.findViewById(R.id.chkPush);
        chkPush.setChecked(GlobalFunction.loadIsPushReceiveAllow(myContext));
        chkPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                Log.d("HWI DEBUG", "푸시 알림 받기 체크 : " + b);
                GlobalFunction.saveIsPushReceive(myContext, b);
            }
        });

        ImageButton btnEasterEgg01 =(ImageButton)v.findViewById(R.id.btnEasterEgg01);
        btnEasterEgg01.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                countOfEasterEggBtnClick++;
                if(countOfEasterEggBtnClick == 10)
                {
                    countOfEasterEggBtnClick = 0;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.URL_EASTER_EGG_VIDEO));
                    intent.setDataAndType(Uri.parse(Const.URL_EASTER_EGG_VIDEO), "video/mp4");
                    startActivity(intent);
                }
            }
        });


        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
    public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(Uri uri);
    }
}
