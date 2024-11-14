package com.example.studentattendance.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentattendance.DataHelper;
import com.example.studentattendance.R;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.text.DateFormatSymbols;

public class SheetActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.titletoolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitletoolbar);
        ImageButton save = toolbar.findViewById(R.id.saveid);
        ImageButton back = toolbar.findViewById(R.id.back);
        Button generatePdfButton = findViewById(R.id.generatePdfButton);

        TextView monthTextView = findViewById(R.id.currentmonthId);

        String month = getIntent().getStringExtra("month");

        // Set the month in the TextView
        if (month != null) {
            monthTextView.setText(month);  // Display the month in the TextView
        }



        back.setOnClickListener(v -> onBackPressed());
        subtitle.setVisibility(View.GONE);
        title.setText("Attendance App");
        generatePdfButton.setText("Generate PDF");
        back.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);

        // Set current month in the TextView
        setCurrentMonth();

        // Show table
        showTable();

        // Set up button to generate PDF
        generatePdfButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                createPdf();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    createPdf();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    private void setCurrentMonth() {
        TextView month=findViewById(R.id.currentmonthId);
        Calendar calendar = Calendar.getInstance();
        int monthIndex = calendar.get(Calendar.MONTH);  // Get the current month (0 = January, 11 = December)
        String currentMonth = new java.text.DateFormatSymbols().getMonths()[monthIndex];  // Get month name
        month.setText(currentMonth);  // Set the month in the TextView
    }

    private String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int monthIndex = calendar.get(Calendar.MONTH);
        String month = new DateFormatSymbols().getMonths()[monthIndex];
        Log.d("CurrentMonth", "Month Index: " + monthIndex + ", Month: " + month);
        return month;  // Should return the correct month
    }

    private void showTable() {
        DataHelper dataHelper = new DataHelper(this);
        TableLayout tableLayout = findViewById(R.id.tablelayoutid);
        long[] idArray = getIntent().getLongArrayExtra("idArray");
        int[] rollArray = getIntent().getIntArrayExtra("rollArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");

        if (idArray == null || rollArray == null || nameArray == null || month == null) {
            Toast.makeText(this, "Data missing from intent", Toast.LENGTH_SHORT).show();
            return;
        }

        int DAY_IN_MONTH = getDAYINMONTH(month);

        // Initialize arrays
        TableRow[] rows = new TableRow[idArray.length + 1];
        TextView[] roll_tvs = new TextView[rows.length];
        TextView[] name_tvs = new TextView[rows.length];
        TextView[][] status_tvs = new TextView[rows.length][DAY_IN_MONTH + 1];
        TextView[] totalPresent_tvs = new TextView[rows.length]; // For Total Present
        TextView[] totalAbsent_tvs = new TextView[rows.length]; // For Total Absent

        for (int i = 0; i < rows.length; i++) {
            roll_tvs[i] = new TextView(this);
            roll_tvs[i].setBackgroundResource(R.drawable.border);  // Set border
            name_tvs[i] = new TextView(this);
            name_tvs[i].setBackgroundResource(R.drawable.border);  // Set border
            totalPresent_tvs[i] = new TextView(this); // Initialize total present column
            totalPresent_tvs[i].setBackgroundResource(R.drawable.border); // Set border for total present
            totalAbsent_tvs[i] = new TextView(this); // Initialize total absent column
            totalAbsent_tvs[i].setBackgroundResource(R.drawable.border); // Set border for total absent
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j] = new TextView(this);
                status_tvs[i][j].setBackgroundResource(R.drawable.border);  // Set border
            }
        }

        // Set up header row
        roll_tvs[0].setText("Roll");
        roll_tvs[0].setTypeface(Typeface.DEFAULT_BOLD);
        name_tvs[0].setText("Name");
        name_tvs[0].setTypeface(Typeface.DEFAULT_BOLD);
        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(Typeface.DEFAULT_BOLD);
        }
        totalPresent_tvs[0].setText("Total Present");
        totalPresent_tvs[0].setTypeface(Typeface.DEFAULT_BOLD);
        totalAbsent_tvs[0].setText("Total Absent");
        totalAbsent_tvs[0].setTypeface(Typeface.DEFAULT_BOLD);

        // Populate table rows
        for (int i = 1; i <= idArray.length; i++) {
            roll_tvs[i].setText(String.valueOf(rollArray[i - 1]));
            name_tvs[i].setText(nameArray[i - 1]);

            int totalPresent = 0; // Counter for total present
            int totalAbsent = 0;  // Counter for total absent

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.format("%02d", j);
                String date = day + "." + month;
                String status = dataHelper.getStatus(idArray[i - 1], date);
                status_tvs[i][j].setText(status);

                // Count present and absent
                if ("P".equals(status)) {
                    totalPresent++;
                    status_tvs[i][j].setTextColor(Color.BLACK); // Set text color to black for present
                } else if ("A".equals(status)) {
                    totalAbsent++;
                    status_tvs[i][j].setTextColor(Color.RED); // Set text color to red for absent
                } else {
                    status_tvs[i][j].setTextColor(Color.BLACK); // Default color for other status
                }
            }

            // Set total present and absent values in the new columns
            totalPresent_tvs[i].setText(String.valueOf(totalPresent));
            totalAbsent_tvs[i].setText(String.valueOf(totalAbsent));
        }

        // Add rows to table layout
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new TableRow(this);
            rows[i].setBackgroundColor(i % 2 == 0 ? Color.parseColor("#EEEEEE") : Color.parseColor("#E4E4E4"));

            roll_tvs[i].setPadding(20, 20, 20, 20);
            name_tvs[i].setPadding(20, 20, 20, 20);
            totalPresent_tvs[i].setPadding(20, 20, 20, 20);
            totalAbsent_tvs[i].setPadding(20, 20, 20, 20);

            rows[i].addView(roll_tvs[i]);
            rows[i].addView(name_tvs[i]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j].setPadding(20, 20, 20, 20);
                rows[i].addView(status_tvs[i][j]);
            }

            // Add Total Present and Total Absent at the end of each row
            rows[i].addView(totalPresent_tvs[i]);
            rows[i].addView(totalAbsent_tvs[i]);

            tableLayout.addView(rows[i]);
        }

        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
    }



    private int getDAYINMONTH(String month) {
        int monthIndex = Integer.parseInt(month.substring(0, 1)) - 1; // Adjust for zero-based index
        int year = Integer.parseInt(month.substring(4));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.YEAR, year);

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void createPdf() {
        String fileName = "AttendanceSheet.pdf";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdfDocument = new PdfDocument(writer);

                    // Set the page size
                    pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
                    Document document = new Document(pdfDocument);

                    // Create a table with columns: Roll, Name, Days of the month, Total Present, Total Absent
                    int columnCount = 2 + getDAYINMONTH(getIntent().getStringExtra("month")) + 2; // 2 (Roll + Name) + Days of month + 2 (Total Present + Total Absent)
                    Table table = new Table(columnCount).useAllAvailableWidth();

                    // Add the header row (including Name column)
                    table.addHeaderCell(new Cell().add(new Paragraph("Roll")).setBold().setBorder(new SolidBorder(1)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Name")).setBold().setBorder(new SolidBorder(1)));

                    // Add columns for each day of the month
                    int daysInMonth = getDAYINMONTH(getIntent().getStringExtra("month"));
                    for (int i = 1; i <= daysInMonth; i++) {
                        table.addHeaderCell(new Cell().add(new Paragraph(String.valueOf(i))).setBold().setBorder(new SolidBorder(1)));
                    }

                    // Add columns for Total Present and Total Absent
                    table.addHeaderCell(new Cell().add(new Paragraph("Total Present")).setBold().setBorder(new SolidBorder(1)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Total Absent")).setBold().setBorder(new SolidBorder(1)));

                    // Populate the table with student data and attendance status
                    DataHelper dataHelper = new DataHelper(this);
                    long[] idArray = getIntent().getLongArrayExtra("idArray");
                    int[] rollArray = getIntent().getIntArrayExtra("rollArray");
                    String[] nameArray = getIntent().getStringArrayExtra("nameArray");
                    String month = getIntent().getStringExtra("month");
                    int DAY_IN_MONTH = getDAYINMONTH(month);

                    for (int i = 0; i < idArray.length; i++) {
                        String roll = String.valueOf(rollArray[i]);
                        String name = nameArray[i];

                        // Initialize counters for total present and total absent
                        int totalPresent = 0;
                        int totalAbsent = 0;

                        // Add the student's roll and name to the row
                        table.addCell(new Cell().add(new Paragraph(roll)).setBorder(new SolidBorder(1)));
                        table.addCell(new Cell().add(new Paragraph(name)).setBorder(new SolidBorder(1)));

                        // Add daily attendance status for each day of the month
                        for (int j = 1; j <= DAY_IN_MONTH; j++) {
                            String day = String.format("%02d", j);
                            String date = day + "." + month;
                            String status = dataHelper.getStatus(idArray[i], date);

                            // Count total present and absent
                            if ("P".equals(status)) {
                                totalPresent++;
                            } else if ("A".equals(status)) {
                                totalAbsent++;
                            }

                            // Add the status (P or A) for the current day
                            table.addCell(new Cell().add(new Paragraph(status == null ? "" : status)).setBorder(new SolidBorder(1)));
                        }

                        // Add the totals for Present and Absent
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalPresent))).setBorder(new SolidBorder(1)));
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalAbsent))).setBorder(new SolidBorder(1)));
                    }

                    // Add the table to the document
                    document.add(table);
                    document.close();

                    Toast.makeText(this, "PDF Created and saved successfully!", Toast.LENGTH_SHORT).show();

                    // Open the PDF
                    openPdf(uri);
                } else {
                    Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create file URI", Toast.LENGTH_SHORT).show();
        }
    }



    private int getColumnCount() {
        TableLayout tableLayout = findViewById(R.id.tablelayoutid);
        if (tableLayout.getChildCount() > 0) {
            TableRow firstRow = (TableRow) tableLayout.getChildAt(0);
            return firstRow.getChildCount(); // Get the number of columns from the first row
        }
        return 0; // Return 0 if no rows exist
    }


    private void openPdf(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPdf();
            } else {
                Toast.makeText(this, "Permission denied to write to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
