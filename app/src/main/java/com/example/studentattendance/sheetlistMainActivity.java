package com.example.studentattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class sheetlistMainActivity extends AppCompatActivity {

    private ListView sheetlist;
    DataHelper dataHelper;
    ArrayList<String> listItem = new ArrayList();
    Toolbar toolbar;
    ArrayAdapter adapter;
    private long cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper=new DataHelper(this);
        setContentView(R.layout.activity_sheetlist_main);
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.titletoolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitletoolbar);
        ImageButton save = toolbar.findViewById(R.id.saveid);
        ImageButton back = toolbar.findViewById(R.id.back);
        title.setText("Attendance App");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);
        back.setOnClickListener(v -> onBackPressed());
        cid = getIntent().getLongExtra("cid", -1);

        loadlistitem();

        sheetlist = findViewById(R.id.sheetlistviewId);
        adapter = new ArrayAdapter(this, R.layout.sheet_list, R.id.date_list_item, listItem);
        sheetlist.setAdapter(adapter);




        sheetlist.setOnItemClickListener((parent, view, position, id) -> opensheetactivity(position));
      //  //  sheetlist.setOnItemClickListener((parent, view, position, id) -> deleteStatus(position)) ;




    }











    private void opensheetactivity(int position) {
        long []idArray=getIntent().getLongArrayExtra("idArray");
        int []rollArray=getIntent().getIntArrayExtra("rollArray");
        String []nameArray =getIntent().getStringArrayExtra("nameArray");
        Intent intent=new Intent(sheetlistMainActivity.this,SheetActivity.class);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        intent.putExtra("month",listItem.get(position));
        startActivity(intent);
    }

    private void loadlistitem() {
        Cursor cursor=new DataHelper(this).getDistinctMonths(cid);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String date= cursor.getString(cursor.getColumnIndex(DataHelper.DATE_KEY));
            listItem.add(date.substring(3));
        }

    }


}

    