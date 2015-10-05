package jhhy.co.kr.taclunchmenu;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

/**
 * Created by jhkim on 2015-04-21.
 */
public class MenuManager
{


    private static MenuManager thisInstance;
    public static ArrayList<Time> todayTotalData;
    public static ArrayList<Day> thisWeekData;

    public interface OnDataReceiveListener
    {
        public void OnDataReceived();
    }



    public static MenuManager getInstance()
    {
        if (thisInstance == null)
        {
            thisInstance = new MenuManager();
            todayTotalData = new ArrayList<Time>();
            thisWeekData = new ArrayList<Day>();
        }
        return thisInstance;
    }

    public class Food
    {
        String foodName;
        boolean isMainFood;
    }


    public class Menu
    {
        public String title;
        public ArrayList<Food> items;
    }

    public class Restaurant
    {
        public String nameOfRestaurant;
        ArrayList<Menu> menus;
    }

    public class Time
    {
        public String time;
        public ArrayList<Restaurant> restaurants;
    }

    public class Day
    {
        public String todayTitleString;
        public ArrayList<Time> time;
    }


    public void getTodayDataFromServer(final OnDataReceiveListener dataListener)
    {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Const.URL_GET_TODAY_MENU, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes)
            {
                String responseString = new String(bytes);
                d("HWI DEBUG", " 전달받은 데이터 : " + responseString);

                Time lunchTime = getTimeFromKeyword(responseString,"점심","lunch");
                Time dinnerTime = getTimeFromKeyword(responseString,"저녁","dinner");
                todayTotalData.add(lunchTime);
                todayTotalData.add(dinnerTime);

                Log.d("HWI DEBUG", "dataListener 객체 확인 : " + dataListener);
                dataListener.OnDataReceived();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable)
            {
                d("HWI DEBUG", "네트워크 에러상황 : i : " + i + " byteString : " + new String(bytes));
            }
        });
    }


    public void getWeekDataFromServer(final OnDataReceiveListener dataListener)
    {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Const.URL_GET_WEEK_MENU, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes)
            {
                String responseString = new String(bytes);
                d("HWI DEBUG", " 전달받은 데이터 : " + responseString);
                try
                {
                    JSONObject weekTotalJSON = new JSONObject(responseString);
                    JSONArray weekTotalJSONArray =  weekTotalJSON.getJSONArray("result");

                    for(int i=0; i<weekTotalJSONArray.length(); i++)
                    {
                        Day oneDayObject = new Day();
                        JSONObject oneDayJSONObject = (JSONObject)weekTotalJSONArray.get(i);
                        oneDayObject.todayTitleString= (String)oneDayJSONObject.get("dayTitle");
                        Time lunchTime = getTimeFromKeyword(oneDayJSONObject.toString(),"점심","lunch");
                        Time dinnerTime = getTimeFromKeyword(oneDayJSONObject.toString(),"저녁","dinner");
                        oneDayObject.time = new ArrayList<Time>();
                        oneDayObject.time.add(lunchTime);
                        oneDayObject.time.add(dinnerTime);
                        Log.d("HWI DEBUG", "oneDayObject.todayTitleString : "+oneDayObject.todayTitleString);
                        thisWeekData.add(oneDayObject);
                    }

                    dataListener.OnDataReceived();

                }
                catch (Exception e)
                {

                }


            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable)
            {
                d("HWI DEBUG", "네트워크 에러상황 : i : " + i + " byteString : " + new String(bytes));
            }
        });
    }


    private Time getTimeFromKeyword(String dataString, String timeTitle, String timeKeyworld)
    {
        Time oneTime = new Time();
        oneTime.time = timeTitle;
        oneTime.restaurants = new ArrayList<Restaurant>();
        try
        {
            Log.d("HWI DEBUG", "dataString : "+dataString);
            JSONObject totalObj = new JSONObject(dataString);
            JSONObject lunchObj = (JSONObject)totalObj.get(timeKeyworld);
            JSONArray restaurantArray =  (JSONArray)lunchObj.get("wellStory");


            Restaurant oneRestaurant = new Restaurant();
            oneRestaurant.nameOfRestaurant = "웰스토리";
            oneRestaurant.menus = new ArrayList<Menu>();
            for(int i=0; i<restaurantArray.length(); i++)
            {
                JSONObject oneMenuObj = (JSONObject)restaurantArray.get(i);

                Menu oneMenu = new Menu();
                oneMenu.title =  oneMenuObj.getString("title");
                JSONArray menuArray =  oneMenuObj.getJSONArray("foods");
                oneMenu.items= jsonArrayToStringArray(menuArray, false);
                oneRestaurant.menus.add(oneMenu);
            }
            oneTime.restaurants.add(oneRestaurant);

            restaurantArray =  (JSONArray)lunchObj.get("ourHome");
            oneRestaurant = new Restaurant();
            oneRestaurant.nameOfRestaurant = "아워홈";
            oneRestaurant.menus = new ArrayList<Menu>();
            for(int i=0; i<restaurantArray.length(); i++)
            {
                JSONObject oneMenuObj = (JSONObject)restaurantArray.get(i);

                Menu oneMenu = new Menu();
                oneMenu.title =  oneMenuObj.getString("title");
                Log.d("HWI DEBUG", "oneMenu.title : "+oneMenu.title);
                JSONArray menuArray =  oneMenuObj.getJSONArray("foods");
                oneMenu.items= jsonArrayToStringArray(menuArray, true);
                oneRestaurant.menus.add(oneMenu);
            }
            oneTime.restaurants.add(oneRestaurant);


        } catch (Exception e)
        {
            Log.d("HWI DEBUG", "메뉴매니저 에러 발생 : "+e.getMessage());
        }

        return oneTime;
    }

    public ArrayList<Food> jsonArrayToStringArray(JSONArray jsonObject, boolean isOurHome)
    {
        ArrayList<Food> list = new ArrayList<Food>();
        JSONArray jsonArray = (JSONArray) jsonObject;

        if (jsonArray != null)
        {
            int len = jsonArray.length();
            int maxLengthOfFoodName=0;
            if(!isOurHome)
            {
                for (int i = 0; i < len; i++)
                {
                    try
                    {
                        if(jsonArray.get(i).toString().length() > maxLengthOfFoodName)
                        {
                            maxLengthOfFoodName =jsonArray.get(i).toString().length();
                        }
                    } catch (Exception e)
                    {
                    }
                }
            }

            for (int i = 0; i < len; i++)
            {
                try
                {
                    Food oneFood = new Food();
                    oneFood.foodName =jsonArray.get(i).toString();
                    oneFood.isMainFood = false;
                    if(!isOurHome)
                    {
                        if(oneFood.foodName.length() == maxLengthOfFoodName)
                        {
                            oneFood.isMainFood = true;
                        }
                    }
                    else
                    {
                        if(i==0)
                        {
                            oneFood.isMainFood = true;
                        }
                    }

                    list.add(oneFood);
                } catch (Exception e)
                {
                    d("HWI DEBUG", "JSON 에서 Array 변환 중 에러 : " + e.getMessage());
                }

            }
        }
        return list;
    }

}
