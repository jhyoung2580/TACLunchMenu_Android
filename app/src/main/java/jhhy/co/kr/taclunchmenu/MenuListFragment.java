package jhhy.co.kr.taclunchmenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import static android.util.Log.d;


public class MenuListFragment extends Fragment
{
    ArrayList<String> groupName;
    HashMap<String, ArrayList<MenuManager.Food>> childs;
    int currentType = 0;
    int currentWeek = 0;
    int currentPosition;

    String KEY_POSITION = "keyPosition";



    public static MenuListFragment newInstance(int type, int currentWeek, int position)
    {
        MenuListFragment oneMenuListFragment = new MenuListFragment();
        oneMenuListFragment.currentPosition = position;
        oneMenuListFragment.currentType = type;
        oneMenuListFragment.currentWeek = currentWeek;
        d("HWI DEBUG", "currentPosition : " + position + "   currentType : " + type + "  currentWeek : " + currentWeek);
        return oneMenuListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_POSITION))
        {
            currentPosition = savedInstanceState.getInt(KEY_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_menu_list, container, false);
        ExpandableListView exlistView = (ExpandableListView) layout.findViewById(R.id.expandableListView01);
        AdapterForExListView adapter = new AdapterForExListView();
        exlistView.setAdapter(adapter);
        exlistView.setClickable(true);

        d("HWI DEBUG", "메뉴리스트 프래그먼트 onCreateView");
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, currentPosition);
    }

    class AdapterForExListView extends BaseExpandableListAdapter
    {

        int countOfWellStory;

        public AdapterForExListView()
        {
            d("HWI DEBUG", "AdapterForExListView 생성자 호출 및 updateData() 함수 실행");
            groupName = new ArrayList<String>();
            childs = new HashMap<String, ArrayList<MenuManager.Food>>();
            updateData();
        }

        public void updateData()
        {
            d("HWI DEBUG", "오늘 메뉴 리스트뷰에 데이터를 씌웁니다. 만약 데이터가 없으면 건너뜁니다.");


            GlobalFunction.doWorkIfOnlineState(getActivity(), new GlobalFunction.OnlineNetworkListener()
            {
                @Override
                public void doWork()
                {
                    MenuManager.Time currentDataTime = null;
                    if(currentType == GlobalFunction.TYPE_MENU_TODAY && MenuManager.todayTotalData.size()>0)
                    {
                        d("HWI DEBUG", "오늘의 메뉴에 진입 확인");
                        currentDataTime = MenuManager.todayTotalData.get(currentPosition);
                    }
                    else if(currentType == GlobalFunction.TYPE_MENU_WEEK && MenuManager.thisWeekData.size()>0)
                    {
                        d("HWI DEBUG", "주간메뉴에 진입 확인");
                        currentDataTime =  MenuManager.thisWeekData.get(currentWeek).time.get(currentPosition);
                    }
                    if(currentDataTime != null)
                    {
                        d("HWI DEBUG", "현재 리스트의 데이터 셋팅 완료됨 확인");
                        groupName.clear();
                        childs.clear();

                        for (int i = 0; i < currentDataTime.restaurants.size(); i++)
                        {
                            MenuManager.Restaurant oneRes = currentDataTime.restaurants.get(i);
                            if (i == 0)
                                countOfWellStory = oneRes.menus.size();

                            Log.d("HWI DEBUG", "웰스토리의 메뉴 수 "+countOfWellStory);

                            for (int j = 0; j < oneRes.menus.size(); j++)
                            {
                                MenuManager.Menu oneMenu = oneRes.menus.get(j);
                                groupName.add(oneMenu.title);
                                childs.put(oneMenu.title, oneMenu.items);
                                for(int k=0; k<oneMenu.items.size(); k++)
                                {
                                    String foodName = oneMenu.items.get(k).foodName;
                                    d("HWI DEBUG", "음식 이름 : "+foodName);
                                }

                            }
                        }
                        android.os.Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                AdapterForExListView.this.notifyDataSetChanged();
                            }
                        },10);

                    }
                }
            });

            }




        @Override
        public int getGroupCount()
        {
            return groupName.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            return (int) Math.ceil(((double) childs.get(groupName.get(i)).size() / 2.0));
        }

        @Override
        public Object getGroup(int i)
        {
            return groupName.get(i);
        }

        @Override
        public Object getChild(int i, int i2)
        {
            return childs.get(groupName.get(i)).get(i2);
        }

        @Override
        public long getGroupId(int i)
        {
            return i;
        }

        @Override
        public long getChildId(int i, int i2)
        {
            return i2;
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
        {
            String title = groupName.get(i);
            Context context = getActivity();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            LinearLayout oneLayout = (LinearLayout) inflater.inflate(R.layout.cell_restaurant, null, false);
            ImageView imgv = (ImageView) oneLayout.findViewById(R.id.thumbnailImageFromCell);
            TextView titleTextFromCell = (TextView) oneLayout.findViewById(R.id.titleTextFromCell);
            titleTextFromCell.setText(title);
            if (i < countOfWellStory)
            {
                imgv.setImageResource(R.drawable.wellstory_logo);
            } else
            {
                imgv.setImageResource(R.drawable.ourhome_logo);
            }

            ExpandableListView eLV = (ExpandableListView) viewGroup;
            eLV.expandGroup(i);

            return oneLayout;
        }

        @Override
        public View getChildView(int i, int i2, boolean b, final View convertView, ViewGroup viewGroup)
        {
            final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout v =(LinearLayout) inflater.inflate(R.layout.cell_food, null, false);


            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View btnView)
                {
                    final String btnString = ((Button) btnView).getText().toString();
                    if (!btnString.equals(""))
                    {
                        GlobalFunction.doWorkIfOnlineState(getActivity(), new GlobalFunction.OnlineNetworkListener()
                        {
                            @Override
                            public void doWork()
                            {
                                RelativeLayout dialogLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_food_image, null, false);
                                final HwiSmartImageView imgViewForFood01 = (HwiSmartImageView) dialogLayout.findViewById(R.id.imgViewForFood01);
                                final HwiSmartImageView imgViewForFood02 = (HwiSmartImageView) dialogLayout.findViewById(R.id.imgViewForFood02);
                                final HwiSmartImageView imgViewForFood03 = (HwiSmartImageView) dialogLayout.findViewById(R.id.imgViewForFood03);
                                final HwiSmartImageView imgViewForFood04 = (HwiSmartImageView) dialogLayout.findViewById(R.id.imgViewForFood04);

                                final ProgressBar progressViewForFood01 = (ProgressBar) dialogLayout.findViewById(R.id.progressViewForFood01);
                                final ProgressBar progressViewForFood02 = (ProgressBar) dialogLayout.findViewById(R.id.progressViewForFood02);
                                final ProgressBar progressViewForFood03 = (ProgressBar) dialogLayout.findViewById(R.id.progressViewForFood03);
                                final ProgressBar progressViewForFood04 = (ProgressBar) dialogLayout.findViewById(R.id.progressViewForFood04);

                                imgViewForFood01.setOnImageChangeListener(new HwiSmartImageView.OnImageChangeListener()
                                {
                                    @Override
                                    public void onChangeImage(HwiSmartImageView hwiv)
                                    {
                                        progressViewForFood01.setVisibility(View.INVISIBLE);
                                    }
                                });

                                imgViewForFood02.setOnImageChangeListener(new HwiSmartImageView.OnImageChangeListener()
                                {
                                    @Override
                                    public void onChangeImage(HwiSmartImageView hwiv)
                                    {
                                        progressViewForFood02.setVisibility(View.INVISIBLE);
                                    }
                                });

                                imgViewForFood03.setOnImageChangeListener(new HwiSmartImageView.OnImageChangeListener()
                                {
                                    @Override
                                    public void onChangeImage(HwiSmartImageView hwiv)
                                    {
                                        progressViewForFood03.setVisibility(View.INVISIBLE);
                                    }
                                });

                                imgViewForFood04.setOnImageChangeListener(new HwiSmartImageView.OnImageChangeListener()
                                {
                                    @Override
                                    public void onChangeImage(HwiSmartImageView hwiv)
                                    {
                                        progressViewForFood04.setVisibility(View.INVISIBLE);
                                    }
                                });

                                AlertDialog.Builder adbd = new AlertDialog.Builder(getActivity());
                                adbd.setTitle(btnString);
                                adbd.setView(dialogLayout);
                                adbd.setPositiveButton("확인", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        dialogInterface.dismiss();
                                    }
                                });
                                adbd.show();


                                AsyncHttpClient client = new AsyncHttpClient();
                                try
                                {
                                    client.get(Const.URL_GOOGLE_IMAGES_SEARCH+ URLEncoder.encode(btnString, "UTF-8"), new AsyncHttpResponseHandler()
                                    {
                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable)
                                        {

                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] bytes)
                                        {
                                            String responseString = new String(bytes);
                                            d("HWI DEBUG", "디버그 responseString : " + responseString);
                                            try
                                            {
                                                JSONObject foodImgJsonURL = new JSONObject(responseString);
                                                JSONObject responseData = foodImgJsonURL.getJSONObject("responseData");
                                                JSONArray results = responseData.getJSONArray("results");
                                                String image01Url = ((JSONObject)results.get(0)).getString("tbUrl");
                                                String image02Url = ((JSONObject)results.get(1)).getString("tbUrl");
                                                String image03Url = ((JSONObject)results.get(2)).getString("tbUrl");
                                                String image04Url = ((JSONObject)results.get(3)).getString("tbUrl");

                                                imgViewForFood01.setImageUrl(image01Url);
                                                imgViewForFood02.setImageUrl(image02Url);
                                                imgViewForFood03.setImageUrl(image03Url);
                                                imgViewForFood04.setImageUrl(image04Url);
                                            } catch (Exception e)
                                            {
                                                d("HWI DEBUG", "JSON 파싱 중 에러 발생 : " + e.getMessage());
                                            }
                                        }
                                    });
                                } catch (Exception e)
                                {
                                    d("HWI DEBUG", "음식 이미지 가져오기 중 에러 발생 : " + e.getMessage());
                                }
                            }
                        });
                    }
                }
            };

            MenuManager.Food oneFood  = childs.get(groupName.get(i)).get(i2 * 2);
            MenuManager.Food twoFood  = null;
            Button cell_food_btn01 = (Button) v.findViewById(R.id.cell_food_btn01);
            String oneChildString01 = oneFood.foodName;
            cell_food_btn01.setText(oneChildString01);

            Button cell_food_btn02 = (Button) v.findViewById(R.id.cell_food_btn02);
            int indexOfString = i2 * 2;
            if (indexOfString < childs.get(groupName.get(i)).size()- 1)
            {
                twoFood = childs.get(groupName.get(i)).get(1 + i2 * 2);
                String oneChildString02 = twoFood.foodName;
                cell_food_btn02.setText(oneChildString02);
            }
            else
            {
                cell_food_btn02.setText("");
            }

            if (i < countOfWellStory)
            {
                cell_food_btn01.setTextColor(0xffccffff);
                cell_food_btn02.setTextColor(0xffccffff);
            } else

            {
                cell_food_btn01.setTextColor(0xffffffff);
                cell_food_btn02.setTextColor(0xffffffff);
            }
            if(oneFood.isMainFood)
            {
                cell_food_btn01.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                cell_food_btn01.setTextColor(0xffff5555);
            }
            if(twoFood != null)
            {
                if(twoFood.isMainFood)
                {
                    cell_food_btn02.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    cell_food_btn02.setTextColor(0xffff5555);
                }
            }

            cell_food_btn01.setOnClickListener(listener);
            cell_food_btn02.setOnClickListener(listener);
            return v;
        }

        @Override
        public boolean isChildSelectable(int i, int i2)
        {
            return false;
        }
    }


}