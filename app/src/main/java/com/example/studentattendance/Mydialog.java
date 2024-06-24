package com.example.studentattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Mydialog extends DialogFragment {
    public static final String ADD_CLASS_DIALOG = "add class";
    public static final String ADD_STUDENT_DIALOG = "add student";
    public static final String ADD_UPDATE_CLASS_DIALOG = "Update class";
    public static final String STUDENT_UPDATE_DIALOG = "Update student";
    OnClickListener listener;
    private int roll;
    private String name;

    public Mydialog(int roll, String name) {

        this.roll = roll;
        this.name = name;
    }

    public Mydialog() {

    }

    public interface OnClickListener {
        void onclick(String text1, String text2);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null;
        if (getTag().equals(ADD_CLASS_DIALOG)) dialog = getaddclassdialog();
        if (getTag().equals(ADD_STUDENT_DIALOG)) dialog = getaddstudentdialog();
        if (getTag().equals(ADD_UPDATE_CLASS_DIALOG)) dialog = getupdateclassdialog();
        if (getTag().equals(STUDENT_UPDATE_DIALOG)) dialog = getupdatestudentdialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        return dialog;
    }

    private Dialog getupdatestudentdialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);

        builder.setView(view);
        TextView title = view.findViewById(R.id.titleId);
        title.setText("Update student");



        EditText rolledt = view.findViewById(R.id.classId);
        EditText  nameedt = view.findViewById(R.id.subjectId);
        rolledt.setHint("Roll");
        nameedt.setHint("Name");
        Button cancelbutton = view.findViewById(R.id.cancelId);
        Button addbuton = view.findViewById(R.id.addId);
        addbuton.setText("Update");
        rolledt.setText(roll+"");
        rolledt.setEnabled(false);
        nameedt.setText(name);
        cancelbutton.setOnClickListener(v -> dismiss());
        addbuton.setOnClickListener(v -> {
            String roll=rolledt.getText().toString();
            String name=nameedt.getText().toString();


            listener.onclick( roll,name);
            dismiss();

        });

        return builder.create();
    }


    private Dialog getaddclassdialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);

        builder.setView(view);
        TextView title = view.findViewById(R.id.titleId);
        title.setText("Add new class");



        EditText classedt = view.findViewById(R.id.classId);
        EditText  subjectedt = view.findViewById(R.id.subjectId);
        classedt.setHint("Classname");
        subjectedt.setHint("Subjectname");
        Button cancelbutton = view.findViewById(R.id.cancelId);
        Button addbuton = view.findViewById(R.id.addId);
        cancelbutton.setOnClickListener(v -> dismiss());
        addbuton.setOnClickListener(v -> {
            String classname=classedt.getText().toString();
            String subjectname=subjectedt.getText().toString();

            listener.onclick( classname,subjectname);
            dismiss();
        });

        return builder.create();
    }
    private Dialog getupdateclassdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);

        builder.setView(view);
        TextView title = view.findViewById(R.id.titleId);
        title.setText("Update class");



        EditText classedt = view.findViewById(R.id.classId);
        EditText  subjectedt = view.findViewById(R.id.subjectId);
        classedt.setHint("Classname");
        subjectedt.setHint("Subjectname");
        Button cancelbutton = view.findViewById(R.id.cancelId);
        Button addbuton = view.findViewById(R.id.addId);
        addbuton.setText("Update");
        cancelbutton.setOnClickListener(v -> dismiss());
        addbuton.setOnClickListener(v -> {
            String classname=classedt.getText().toString();
            String subjectname=subjectedt.getText().toString();

            listener.onclick( classname,subjectname);
            dismiss();
        });

        return builder.create();
    }

    private Dialog getaddstudentdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);

        builder.setView(view);
        TextView title = view.findViewById(R.id.titleId);
        title.setText("Add new student");



        EditText rolledt = view.findViewById(R.id.classId);
        EditText  nameedt = view.findViewById(R.id.subjectId);
        rolledt.setHint("Roll");
        nameedt.setHint("Name");
        Button cancelbutton = view.findViewById(R.id.cancelId);
        Button addbuton = view.findViewById(R.id.addId);
        cancelbutton.setOnClickListener(v -> dismiss());
        addbuton.setOnClickListener(v -> {
            String roll=rolledt.getText().toString();
            String name=nameedt.getText().toString();
            rolledt.setText(String.valueOf(Integer.parseInt(roll)+1));
            nameedt.setText("");

            listener.onclick( roll,name);

        });

        return builder.create();

    }



}
