package com.example.myweather;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class MyConcernList extends AppCompatActivity {

    ArrayAdapter simpleAdapter;
    ListView MyConcernList;
    private List<String> city_nameList = new ArrayList<>();
    private List<String> city_codeList = new ArrayList<>();

    private void InitConcern() {       //初始化
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Concern.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor  = db.rawQuery("select * from Concern",null);
        while(cursor.moveToNext()){
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
            city_codeList.add(city_code);
            city_nameList.add(city_name);
        }
    }

    public void RefreshList(){//刷新ListView
        city_nameList.removeAll(city_nameList);
        city_codeList.removeAll(city_codeList);
        simpleAdapter.notifyDataSetChanged();
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Concern.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor  = db.rawQuery("select * from Concern",null);
        while(cursor.moveToNext()){
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
            city_codeList.add(city_code);
            city_nameList.add(city_name);
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        RefreshList();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_concern_list);
        MyConcernList = findViewById(R.id.MyConcernList);

        InitConcern();

        simpleAdapter = new ArrayAdapter(MyConcernList.this,android.R.layout.simple_list_item_1,city_nameList);
        //适配器
        MyConcernList.setAdapter(simpleAdapter);
        MyConcernList.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                String tran = city_codeList.get(position);
                Intent intent = new Intent(MyConcernList.this, com.example.myweather.Weather.class);//打开天气页面
                intent.putExtra("trancitycode",tran);
                startActivity(intent);
            }
        });
    }
}
