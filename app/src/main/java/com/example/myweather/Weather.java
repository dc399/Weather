package com.example.myweather;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity implements View.OnClickListener{
    TextView Textshow;
    String researchcitycode;
    Button Concern,refresh;
    String cityStr;

    private String city;
    private App.forecast fc0, fc1, fc2, fc3, fc4;
    private String cityId;
    private String parent;
    private String updateT;
    private String t;
    private String date;
    private String mes;
    private String status;
    private String shidu;
    private String pm25;
    private String pm10;
    private String quality;
    private String tem;
    private String ill;
    private List<App.forecast> forecasts;
    private String ymd;
    private String date_1;
    private String week;
    private String sunRiseT;
    private String highTem;
    private String lowTem;
    private String sunSetT;
    private String aqi;
    private String fx;
    private String fl;
    private String type;
    private String notice;
    //今天和未来四天的天气情况
    private String d0, d1, d2, d3, d4;//今天和未来四天的date
    private String ymd0,ymd1,ymd2,ymd3,ymd4;//今天和未来四天的日期
    private String week0,week1,week2,week3,week4;//今天和未来四天的星期
    private String sunRiseT0, sunRiseT1, sunRiseT2, sunRiseT3, sunRiseT4;//今天和未来四天的日出时间
    private String highTem0, highTem1, highTem2, highTem3, highTem4;//最高温度
    private String lowTem0, lowTem1, lowTem2, lowTem3, lowTem4;//最低温度
    private String sunSetT0, sunSetT1, sunSetT2, sunSetT3, sunSetT4;//日落时间
    private String aqi0,aqi1,aqi2,aqi3,aqi4;//aqi
    private String fx0,fx1,fx2,fx3,fx4;//fx
    private String fl0,fl1,fl2,fl3,fl4;//fl
    private String type0,type1,type2,type3,type4;//type
    private String notice0,notice1,notice2,notice3,notice4;//注意事项
    int databaseid;
    String databasedata;
    int sign = 1;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Textshow = findViewById(R.id.TextView);//天气信息显示
        Concern = findViewById(R.id.concern1);
        Concern.setOnClickListener(this);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(this);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        researchcitycode = extras.getString("trancitycode");


        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Weather.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Weather",new String[]{"id","data"},"id=?",new String[]{researchcitycode+""},null,null,null);

        if(cursor.moveToFirst()) {       //查找数据库，若有缓存则从数据库中读取
            do {
                databaseid = cursor.getInt(cursor.getColumnIndex("id"));
                databasedata = cursor.getString(cursor.getColumnIndex("data"));
            } while (cursor.moveToNext());
            cursor.close();
        }
        int tranformat = 0;
        tranformat = Integer.parseInt(researchcitycode);
        if(databaseid ==  tranformat ){
            sign = 1;
            showResponse(databasedata);
        }
        else{
            sign = 0;
            //Toast.makeText(this,"执行sendRequestWithOkHttp()",Toast.LENGTH_SHORT).show();
            sendRequestWithOkHttp();//若数据库中没有缓存，访问在线天气API，请求数据
        }

    }


    private void sendRequestWithOkHttp(){//访问在线天气API，请求数据
        //Log.d("data is", "0");
        //Toast.makeText(this,"sendRequestWithOkHttp()",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Log.d("data is", "1");
                    OkHttpClient client = new OkHttpClient();
                    //Log.d("data is", "2");
                    Request request = new Request.Builder()
                            .url("http://t.weather.itboy.net/api/weather/city/"+researchcitycode)
                            .build();
                    //Log.d("data is", "3");
                    Response response = client.newCall(request).execute();
                    //Log.d("data is", "4");
                    String responseData = response.body().string();
                    //Log.d("data is", responseData);
                    showResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }).start();
    }




    private void parseJSONWithFastJSON(String jsonData){
        if(jsonData.length()<100){
            Log.d("M","城市ID不存在");
            Toast.makeText(this,"城市ID不存在，请重新输入",Toast.LENGTH_LONG).show();
            Weather.this.setResult(RESULT_OK,getIntent());
            Weather.this.finish();
        }
        else {
            App app = JSON.parseObject(jsonData, App.class);
            t = app.getTime();
            mes = app.getMessage();
            status = app.getStatus();
            date = app.getDate();


            App.CityInfo cityInfo = app.getCityInfo();
            city = cityInfo.getCity();
            cityId = cityInfo.getCityId();
            parent = cityInfo.getParent();
            updateT = cityInfo.getUpdateTime();


            App.data data = app.getData();
            shidu = data.getShidu();//得到湿度
            pm10 = data.getPm10();//得到pm10
            pm25 = data.getPm25();//得到pm25
            quality = data.getQuality();//得到空气质量
            ill = data.getGanmao();//得到感冒建议
            tem = data.getWendu();//得到温度
            forecasts = data.getForecast();
//今天和未来四天的天气情况
            fc0 = forecasts.get(0);
            d0 = fc0.getDate();
            highTem0 = fc0.getHigh();//得到今天的最高温度
            lowTem0 = fc0.getLow();//得到今天的最低温度
            week0 = fc0.getWeek();//得到今天的星期
            sunRiseT0 = fc0.getSunrise();//得到今天的日出时间
            ymd0 = fc0.getYmd();//得到今天的日期
            sunSetT0 = fc0.getSunset();//得到今天的日落时间
            aqi0 = fc0.getAqi();
            fx0 = fc0.getFx();
            fl0 = fc0.getFl();
            notice0 = fc0.getNotice();//得到今天的注意事项
            type0 = fc0.getType();

            fc1 = forecasts.get(1);
            d1 = fc1.getDate();
            highTem1 = fc1.getHigh();//得到未来一天的最高温度
            lowTem1 = fc1.getLow();//得到未来一天的最低温度
            week1 = fc1.getWeek();//得到未来一天的星期
            sunRiseT1 = fc1.getSunrise();//得到未来一天的日出时间
            ymd1 = fc1.getYmd();//得到未来一天的日期
            sunSetT1 = fc1.getSunset();//得到未来一天的日落时间
            aqi1 = fc1.getAqi();
            fx1 = fc1.getFx();
            fl1 = fc1.getFl();
            notice1 = fc1.getNotice();//得到未来一天的注意事项
            type1 = fc1.getType();


            fc2 = forecasts.get(2);
            d2 = fc2.getDate();
            highTem2 = fc2.getHigh();//得到未来二天的最高温度
            lowTem2 = fc2.getLow();//得到未来二天的最低温度
            week2 = fc2.getWeek();//得到未来二天的星期
            sunRiseT2 = fc2.getSunrise();//得到未来二天的日出时间
            ymd2 = fc2.getYmd();//得到未来二天的日期
            sunSetT2 = fc2.getSunset();//得到未来二天的日落时间
            aqi2 = fc2.getAqi();
            fx2 = fc2.getFx();
            fl2 = fc2.getFl();
            notice2 = fc2.getNotice();//得到未来二天的注意事项
            type2 = fc2.getType();

            fc3 = forecasts.get(3);
            d3 = fc3.getDate();
            highTem3 = fc3.getHigh();//得到未来三天的最高温度
            lowTem3 = fc3.getLow();//得到未来三天的最低温度
            week3 = fc3.getWeek();//得到未来三天的星期
            sunRiseT3 = fc3.getSunrise();//得到未来三天的日出时间
            ymd3 = fc3.getYmd();//得到未来三天的日期
            sunSetT3 = fc3.getSunset();//得到未来三天的日落时间
            aqi3 = fc3.getAqi();
            fx3 = fc3.getFx();
            fl3 = fc3.getFl();
            notice3 = fc3.getNotice();//得到未来三天的日落时间
            type3 = fc3.getType();


            fc4 = forecasts.get(4);
            d4 = fc4.getDate();
            highTem4 = fc4.getHigh();//得到未来四天的最高温度
            lowTem4 = fc4.getLow();//得到未来四天的最低温度
            week4 = fc4.getWeek();//得到未来四天的星期
            sunRiseT4 = fc4.getSunrise();//得到未来四天的日出时间
            ymd4 = fc4.getYmd();//得到未来四天的日期
            sunSetT4 = fc4.getSunset();//得到未来四天的日落时间
            aqi4 = fc4.getAqi();
            fx4 = fc4.getFx();
            fl4 = fc4.getFl();
            notice4 = fc4.getNotice();//得到未来四天的日落时间
            type4 = fc4.getType();


            App.data.yesterday yesterday = data.getYesterday();
            ymd = yesterday.getYmd();//得到昨天的日期
            week = yesterday.getWeek();//得到昨天的星期
            sunRiseT = yesterday.getSunrise();//得到昨天的日出时间
            highTem = yesterday.getHigh();//得到昨天的最高温度
            lowTem = yesterday.getLow();//得到昨天的最低温度
            sunSetT = yesterday.getSunset();//得到昨天的日落时间
            aqi = yesterday.getAqi();
            fl = yesterday.getFl();
            fx = yesterday.getFx();
            notice = yesterday.getNotice();//得到昨天的注意事项
            type = yesterday.getType();
            date_1 = yesterday.getDate();

            if (sign == 0) {
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", researchcitycode);
                values.put("data", jsonData);
                db.insert("Weather", null, values);
                Log.d("MainActivity", "数据库写入成功");
            } else if (sign == 1) {
                Log.d("数据库写入失败：", "数据已存在");

            } else {
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", researchcitycode);
                values.put("data", jsonData);
                db.update("Weather", values, "id=?", new String[]{researchcitycode + ""});
                Log.d("MainActivity", "数据库更新成功");

            }

        }
    }



    private void showResponse(final String response){
        //Toast.makeText(this,"showResponse",Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parseJSONWithFastJSON(response);
                Log.d("MainActivity", "parseJSONWithFastJSON(response)");
                String CityshowString;
                CityshowString = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                CityshowString = CityshowString+"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                CityshowString = CityshowString+"当前日期:"+ymd0+"\n"+week0+"\n"+"日出时间:"+ sunRiseT0 +"\n"+"最高温度:"+ highTem0 +"\n"+"最低温度:"+ lowTem0 +"\n"+"日落时间："+ sunSetT0 +"\n"+"空气指数："+aqi0+"\n"+"风力："+fl0+"\n"+"风向："+fx0+"\n"+"提示:"+notice0+"\n"+"天气:"+type0;
                Textshow.setText(CityshowString);
                Log.d("MainActivity", "显示成功");
            }
        });

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            /**
            *关注
             */
            case R.id.concern1:
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Concern.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("city_code", researchcitycode);
                values.put("city_name", city);
                db.insert("Concern", null, values);         //加入数据库中的关注表
                Toast.makeText(this, "关注成功！", Toast.LENGTH_LONG).show();
                break;
            /**
             *刷新
             */
            case  R.id.refresh:
                sign = 3;
                sendRequestWithOkHttp();//访问在线天气API，请求数据
                Log.d("MainActivity","数据库刷新成功");
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.yesterday:
                cityStr = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                cityStr = cityStr +"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                cityStr = cityStr +"当前日期:"+ymd+"\n"+week+"\n"+"日出时间:"+ sunRiseT +"\n"+"最高温度:"+ highTem +"\n"+"最低温度:"+ lowTem +"\n"+"日落时间："+ sunSetT +"\n"+"空气指数："+aqi+"\n"+"风力："+fl+"\n"+"风向："+fx+"\n"+"提示:"+notice+"\n"+"天气:"+type;
                Textshow.setText(cityStr);
                break;
            case  R.id.today:
                cityStr = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                cityStr = cityStr +"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                cityStr = cityStr +"当前日期:"+ymd0+"\n"+week0+"\n"+"日出时间:"+ sunRiseT0 +"\n"+"最高温度:"+ highTem0 +"\n"+"最低温度:"+ lowTem0 +"\n"+"日落时间："+ sunSetT0 +"\n"+"空气指数："+aqi0+"\n"+"风力："+fl0+"\n"+"风向："+fx0+"\n"+"提示:"+notice0+"\n"+"天气:"+type0;

                Textshow.setText(cityStr);
                break;
            case R.id.forecast1:
                cityStr = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                cityStr = cityStr +"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                cityStr = cityStr +"当前日期:"+ymd1+"\n"+week1+"\n"+"日出时间:"+ sunRiseT1 +"\n"+"最高温度:"+ highTem1 +"\n"+"最低温度:"+ lowTem1 +"\n"+"日落时间："+ sunSetT1 +"\n"+"空气指数："+aqi1+"\n"+"风力："+fl1+"\n"+"风向："+fx1+"\n"+"提示:"+notice1+"\n"+"天气:"+type1;
                Textshow.setText(cityStr);
                break;
            case  R.id.forecast2:
                cityStr = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                cityStr = cityStr +"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                cityStr = cityStr +"当前日期:"+ymd2+"\n"+week2+"\n"+"日出时间:"+ sunRiseT2 +"\n"+"最高温度:"+ highTem2 +"\n"+"最低温度:"+ lowTem2 +"\n"+"日落时间："+ sunSetT2 +"\n"+"空气指数："+aqi2+"\n"+"风力："+fl2+"\n"+"风向："+fx2+"\n"+"提示:"+notice2+"\n"+"天气:"+type2;
                Textshow.setText(cityStr);
                break;
            case  R.id.forecast3:
                cityStr = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                cityStr = cityStr +"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                cityStr = cityStr +"当前日期:"+ymd3+"\n"+week3+"\n"+"日出时间:"+ sunRiseT3 +"\n"+"最高温度:"+ highTem3 +"\n"+"最低温度:"+ lowTem3 +"\n"+"日落时间："+ sunSetT3 +"\n"+"空气指数："+aqi3+"\n"+"风力："+fl3+"\n"+"风向："+fx3+"\n"+"提示:"+notice3+"\n"+"天气:"+type3;
                Textshow.setText(cityStr);
                break;
            case R.id.forecast4:
                cityStr = "数据更新时间:"+ t +"\n"+"当前状态："+ mes +"\n"+"状态号:"+status+"\n"+"当前日期:"+date+ "\n"+"当前城市:"+city+"\n"+"城市ID:"+cityId+"\n"+"所在省:"+parent+"\n"+"更新时间"+ updateT;
                cityStr = cityStr +"\n"+"空气湿度"+shidu+"\n"+"pm10:"+pm10+"\n"+"pm2.5:"+pm25+"\n"+"空气质量:"+quality+"\n"+"活动适宜群体:"+ ill +"\n"+"当前温度"+ tem +"\n";
                cityStr = cityStr +"当前日期:"+ymd4+"\n"+week4+"\n"+"日出时间:"+ sunRiseT4 +"\n"+"最高温度:"+ highTem4 +"\n"+"最低温度:"+ lowTem4 +"\n"+"日落时间："+ sunSetT4 +"\n"+"空气指数："+aqi4+"\n"+"风力："+fl4+"\n"+"风向："+fx4+"\n"+"提示:"+notice4+"\n"+"天气:"+type4;
                Textshow.setText(cityStr);
                break;
            case R.id.cancel_concern:
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Concern.db",null,1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Concern","city_code=?",new String[]{researchcitycode+""});
                Toast.makeText(this,"取消关注成功",Toast.LENGTH_LONG).show();
                Weather.this.setResult(RESULT_OK,getIntent());
                Weather.this.finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
