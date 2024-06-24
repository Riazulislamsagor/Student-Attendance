package com.example.studentattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class SheetActivity extends AppCompatActivity {
    Context context;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        toolbar=findViewById(R.id.toolbar);
        TextView title=toolbar.findViewById(R.id.titletoolbar);
        TextView subtitle=toolbar.findViewById(R.id.subtitletoolbar);
        ImageButton save=toolbar.findViewById(R.id.saveid);
        ImageButton back=toolbar.findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        title.setText("Attendance App");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);



        showTable();

    }

    private void showTable() {
        DataHelper dataHelper=new DataHelper(this);
    TableLayout tableLayout=findViewById(R.id.tablelayoutid);
    long []idArray=getIntent().getLongArrayExtra("idArray");
    int []rollArray=getIntent().getIntArrayExtra("rollArray");
    String []nameArray =getIntent().getStringArrayExtra("nameArray");
    String month =getIntent().getStringExtra("month");

    int DAY_IN_MONTH=getDAYINMONTH(month);

    //row  setup

    int rowsize=idArray.length+1;

    TableRow[] rows=new TableRow[rowsize];
    TextView[] roll_tvs=new TextView[rowsize];
    TextView[] name_tvs=new TextView[rowsize];
    TextView[][] status_tvs=new TextView[rowsize][DAY_IN_MONTH+1];
    for(int i=0;i<rowsize;i++){
         roll_tvs[i]=new TextView(this);
         name_tvs[i]=new TextView(this);
         for(int j=1;j<=DAY_IN_MONTH;j++){
             status_tvs[i][j]=new TextView(this);
         }

    }

    //header
        roll_tvs[0].setText("roll");
    roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
        name_tvs[0].setText("name");
        name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);
        for(int i=1;i<=DAY_IN_MONTH;i++){
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(),Typeface.BOLD);
        }

        for (int i=1;i<rowsize;i++){
            roll_tvs[i].setText(String.valueOf(rollArray[i-1]));
            name_tvs[i].setText(nameArray[i-1]);
            for(int j=1;j<=DAY_IN_MONTH;j++){

                String day=String.valueOf(j);
                if(day.length()==1) day="0"+day;
                String date=day+"."+month;
                String status=dataHelper.getStatus(idArray[i-1],date);
                status_tvs[i][j].setText(status);

            }
        }
        for (int i=0;i<rowsize;i++){
            rows[i]=new TableRow(this);
            if (i%2==0){
                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
                rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));
            }

            roll_tvs[i].setPadding(18,18,18,18);
            name_tvs[i].setPadding(18,18,18,18);

            rows[i].addView(roll_tvs[i]);
            rows[i].addView(name_tvs[i]);
            for(int j=1;j<=DAY_IN_MONTH;j++){
                status_tvs[i][j].setPadding(18,18,18,18);
                rows[i].addView(status_tvs[i][j]);
            }
            tableLayout.addView(rows[i]);
            tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);

        }
    }



    private int getDAYINMONTH(String month) {
        int monthIndex=Integer.parseInt(month.substring(0,1));
        int year=Integer.parseInt(month.substring(4));

        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MONTH,monthIndex);
        calendar.set(Calendar.YEAR,year);

    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}