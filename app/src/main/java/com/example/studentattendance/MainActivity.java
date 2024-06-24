package com.example.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fabbtn;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ClassAdapter classAdapter;
    DataHelper dataHelper;
    ArrayList<class_items> classItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabbtn = findViewById(R.id.fabId);
        dataHelper=new DataHelper(this);

        fabbtn.setOnClickListener(v -> showDialog());
        loadData();

        recyclerView = findViewById(R.id.recycleaviewId);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoactivity(position));
        settoolbar();
    }

    private void loadData() {

        Cursor cursor=dataHelper.getClassTable();
        classItems.clear();
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id=cursor.getInt(cursor.getColumnIndex(DataHelper.C_ID));
            @SuppressLint("Range") String className=cursor.getString(cursor.getColumnIndex(DataHelper.CLASS_NAME_KEY));
            @SuppressLint("Range") String subjectName=cursor.getString(cursor.getColumnIndex(DataHelper.SUBJECT_NAME_KEY));
            classItems.add(new class_items(id,className,subjectName));
        }
    }

    private void settoolbar() {
            toolbar=findViewById(R.id.toolbar);
            TextView title=toolbar.findViewById(R.id.titletoolbar);
            TextView subtitle=toolbar.findViewById(R.id.subtitletoolbar);
            ImageButton save=toolbar.findViewById(R.id.saveid);
            ImageButton back=toolbar.findViewById(R.id.back);

            title.setText("Attendance App");
            subtitle.setVisibility(View.GONE);
            back.setVisibility(View.INVISIBLE);
            save.setVisibility(View.INVISIBLE);


        }




    private void gotoactivity(int position) {
        Intent intent = new Intent(this, StudentActivity.class);
        intent.putExtra("className",classItems.get(position).getClassName());
        intent.putExtra("subjectName",classItems.get(position).getSubjectName());
        intent.putExtra("position",position);
        intent.putExtra("cid",classItems.get(position).getCid());
        startActivity(intent);
    }


    private void showDialog() {
        Mydialog mydialog = new Mydialog();
        mydialog.show(getSupportFragmentManager(),Mydialog.ADD_CLASS_DIALOG);
        mydialog.setListener((className,subjectName)->addclss(className,subjectName));



    }

    private void addclss(String className,String subjectName) {
        long cid=dataHelper.addClass(className,subjectName);
        class_items class_item=new class_items(cid,className,subjectName);

        classItems.add(class_item);
        classAdapter.notifyDataSetChanged();


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showupdateDialog(item.getGroupId());
            break;
            case 1:
            deleteclass(item.getGroupId());
        }

        return super.onContextItemSelected(item);
    }

    private void showupdateDialog(int position) {
        Mydialog mydialog=new Mydialog();
        mydialog.show(getSupportFragmentManager(),Mydialog.ADD_UPDATE_CLASS_DIALOG);
        mydialog.setListener((className,subjectName)->updateclss(position,className,subjectName));
    }

    private void updateclss(int position, String className, String subjectName) {
        dataHelper.updateclss(classItems.get(position).getCid(),className,subjectName);
        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }

    private void deleteclass(int position) {
        dataHelper.deleteClass(classItems.get(position).getCid());
        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);
    }

}

