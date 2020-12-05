package com.example.myweather;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myweather.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private List<Integer> idList = new ArrayList<>();
    private List<Integer> pidList = new ArrayList<>();
    private List<String> city_nameList = new ArrayList<>();
    private List<String> city_codeList = new ArrayList<>();
    ArrayAdapter simpleAdapter;
    Button OK,MyConcern;
    EditText Research;
    ListView ProvinceList;


    private void parseJSONWithJSONObject(String jsonData){//将getJson()得到的Json字符串转换为JSONObject的数组

        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Integer id = jsonObject.getInt("id");
                Integer pid = jsonObject.getInt("pid");
                String city_code = jsonObject.getString("city_code");
                String city_name = jsonObject.getString("city_name");
                if(pid == 0 ) {
                    idList.add(id);
                    pidList.add(pid);
                    city_codeList.add(city_code);
                    city_nameList.add(city_name);
                }
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
        setContentView(R.layout.activity_main);
        OK = findViewById(R.id.ok);
        Research = findViewById(R.id.research);
        ProvinceList = findViewById(R.id.provincelist);
        OK.setOnClickListener(this);
        MyConcern = findViewById(R.id.myconcern);
        MyConcern.setOnClickListener(this);

        String responseData = getJson("city.json",this);
        parseJSONWithJSONObject(responseData);//读取city.json中的json内容


        simpleAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,city_nameList);
        //适配器
        ProvinceList.setAdapter(simpleAdapter);
        ProvinceList = findViewById(R.id.provincelist);
        ProvinceList.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                int tran = idList.get(position);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("tran",tran);
                startActivity(intent);
            }
        });




    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ok://查询
                String researchcitycode = String.valueOf(Research.getText());
                if(researchcitycode.length()>9){
                    Toast.makeText(this,"数字长度不能大于九位！",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, Weather.class);
                    intent.putExtra("trancitycode", researchcitycode);
                    startActivity(intent);
                }
                break;
            case R.id.myconcern://打开关注列表
                Intent intent = new Intent(MainActivity.this,MyConcernList.class);
                startActivity(intent);
                break;
        }
    }



}
