package com.example.myweather;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    ListView CityList;
    int tranid = 0;
    ArrayAdapter simpleAdapter;
    private List<Integer> idList = new ArrayList<>();
    private List<Integer> pidList = new ArrayList<>();
    private List<String> city_nameList = new ArrayList<>();
    private List<String> city_codeList = new ArrayList<>();


    private void parseJSONWithJSONObject(String jsonData){//将getJson()得到的Json字符串转换为JSONObject的数组
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Integer id = jsonObject.getInt("id");
                Integer pid = jsonObject.getInt("pid");
                String city_code = jsonObject.getString("city_code");
                String city_name = jsonObject.getString("city_name");
                if(pid == tranid ) {
                    idList.add(id);
                    pidList.add(pid);
                    city_codeList.add(city_code);
                    city_nameList.add(city_name);
                }
            }
            if(tranid == 33){
                idList.add(33);
                pidList.add(0);
                city_codeList.add("101330101");
                city_nameList.add("澳门");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getJson(String fileName, Context context) {//得到assets文件夹下的city.json的字符串形式的json
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        CityList = findViewById(R.id.citylist);

        Intent intent = getIntent();
        tranid = intent.getIntExtra("tran",-1);

        String responseData = getJson("city.json",this);
        parseJSONWithJSONObject(responseData);


        simpleAdapter = new ArrayAdapter(SecondActivity.this,android.R.layout.simple_list_item_1,city_nameList);

        CityList.setAdapter(simpleAdapter);
        CityList = findViewById(R.id.citylist);
        CityList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                String trancode = city_codeList.get(position);
                Intent intent = new Intent(SecondActivity.this, Weather.class);
                intent.putExtra("trancitycode",trancode);
                startActivity(intent);
            }
        });


    }
}


