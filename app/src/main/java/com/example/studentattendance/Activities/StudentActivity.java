package com.example.studentattendance.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.studentattendance.DataHelper;
import com.example.studentattendance.MyCalender;
import com.example.studentattendance.Mydialog;
import com.example.studentattendance.R;
import com.example.studentattendance.StudentAdapter;
import com.example.studentattendance.StudentItem;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    Toolbar toolbar;
    String className,subjectName;
    int position;
    RecyclerView recyclerView;
    ArrayList<StudentItem> studentItem = new ArrayList<>();
    StudentAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    DataHelper dataHelper;
    private long cid;
    private MyCalender calender;
    TextView subtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        dataHelper = new DataHelper(this);


        calender = new MyCalender();

        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position", -1);
        cid = intent.getLongExtra("cid", -1);

        setToolbar();
        loadData();

        recyclerView = findViewById(R.id.studentrecycleviewId);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(this, studentItem, dataHelper);
         RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> changestatus(position));
        loadStatusData();
    }

    private void loadData() {
        Cursor cursor = dataHelper.getStudentTable(cid);
        Log.i("1234567890", "loadData: " + cid);
        studentItem.clear();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") long sid = cursor.getLong(cursor.getColumnIndex(DataHelper.S_ID));
            @SuppressLint("Range") int roll = cursor.getInt(cursor.getColumnIndex(DataHelper.STUDENT_ROLL_KEY));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DataHelper.STUDENT_NAME_KEY));

            studentItem.add(new StudentItem(sid, roll, name));
        }
        cursor.close();
    }

    private void changestatus(int position) {
        String status = studentItem.get(position).getStatus();
        if ("P".equals(status)) status = "A";
        else  status = "P";
        studentItem.get(position).setStatus(status);
        adapter.notifyItemChanged(position);
          // Update counts after changing status
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.titletoolbar);
        subtitle = toolbar.findViewById(R.id.subtitletoolbar);
        ImageButton save = toolbar.findViewById(R.id.saveid);
        ImageButton back = toolbar.findViewById(R.id.back);
        save.setOnClickListener(v -> saveStatus());

        title.setText(className);
        subtitle.setText(subjectName + " | " + calender.getDate());
        back.setOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private void saveStatus() {
        for (StudentItem studentItem1 : studentItem) {
            String status = studentItem1.getStatus();
            if (status == null || "".equals(status)) status = "A";  // Default to "A" if status is empty
            long value = dataHelper.addStatus(studentItem1.getSid(), cid, calender.getDate(), status);
            if (value == -1) dataHelper.updateStatus(studentItem1.getSid(), calender.getDate(), status);
        }
    }

    private void loadStatusData() {
        for (StudentItem studentItem1 : studentItem) {
            String status = dataHelper.getStatus(studentItem1.getSid(), calender.getDate());
            if (status != null) studentItem1.setStatus(status);
            else studentItem1.setStatus("");
        }
        adapter.notifyDataSetChanged();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.addstudentId) {
            showStudentDialog();
        } else if (menuItem.getItemId() == R.id.showdateId) {
            showdateDialog();
        } else if (menuItem.getItemId() == R.id.attendancesheetId) {
            openshitlist();
        }
        return true;
    }

    private void openshitlist() {
        long[] idArray = new long[studentItem.size()];
        int[] rollArray = new int[studentItem.size()];
        String[] nameArray = new String[studentItem.size()];
        for (int i = 0; i < idArray.length; i++)
            idArray[i] = studentItem.get(i).getSid();
        for (int i = 0; i < rollArray.length; i++)
            rollArray[i] = studentItem.get(i).getRoll();
        for (int i = 0; i < nameArray.length; i++)
            nameArray[i] = studentItem.get(i).getName();
        Intent intent = new Intent(this, sheetlistMainActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("idArray", idArray);
        intent.putExtra("rollArray", rollArray);
        intent.putExtra("nameArray", nameArray);
        startActivity(intent);
    }

    private void showdateDialog() {
        calender.show(getSupportFragmentManager(), "");
        calender.setOnCalenderokClicklistener(this::onCalenderokClick);
    }

    private void onCalenderokClick(int year, int month, int day) {
        calender.setDate(year, month, day);
        subtitle.setText(subjectName + " | " + calender.getDate());
        loadStatusData();
    }

    private void showStudentDialog() {
        Mydialog mydialog = new Mydialog();
        mydialog.show(getSupportFragmentManager(), Mydialog.ADD_STUDENT_DIALOG);
        mydialog.setListener((roll, name) -> addstudent(roll, name));
    }

    private void addstudent(String roll_string, String name) {
        int roll = Integer.parseInt(roll_string);
        long sid = dataHelper.addStudent(cid, roll, name);
        StudentItem studentItem1 = new StudentItem(sid, roll, name);
        studentItem.add(studentItem1);
        adapter.notifyDataSetChanged();
    }

    private void updateCounts() {
        String date = calender.getDate();
        int presentCount = dataHelper.getPresentCount(cid, date);
        int absentCount = dataHelper.getAbsentCount(cid, date);


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showupdatestudentDialog(item.getGroupId());
                break;
            case 1:
                deletestudent(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showupdatestudentDialog(int position) {
        Mydialog mydialog = new Mydialog(studentItem.get(position).getRoll(), studentItem.get(position).getName());
        mydialog.show(getSupportFragmentManager(), Mydialog.STUDENT_UPDATE_DIALOG);
        mydialog.setListener((roll_string, name) -> updateStudent(position, name));
    }

    private void updateStudent(int position, String name) {
        dataHelper.updateStudent(studentItem.get(position).getSid(), name);
        studentItem.get(position).setName(name);
        adapter.notifyItemChanged(position);
    }

    private void deletestudent(int position) {
        dataHelper.deleteStudent(studentItem.get(position).getSid());
        studentItem.remove(position);
        adapter.notifyItemRemoved(position);
    }
}