package com.example.studentattendance;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

ArrayList<class_items> classItems;
Context context;
private OnItemClickListener onItemClickListener;
public interface OnItemClickListener{
    void onclick(int position);
}

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ClassAdapter(Context context, ArrayList<class_items> classItems) {
        this.context=context;

        this.classItems = classItems;

    }

    public class ClassViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView classname,subjectname;
        public ClassViewHolder(@NonNull View itemView,OnItemClickListener onItemClickListener ) {
            super(itemView);
         classname=   itemView.findViewById(R.id.class_Id);
         subjectname=   itemView.findViewById(R.id.subjectId);
         itemView.setOnClickListener(v -> onItemClickListener.onclick(getAdapterPosition()));
         itemView.setOnCreateContextMenuListener(this);
         
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),0,0,"Edit");
            menu.add(getAdapterPosition(),1,1,"Delete");
        }
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,null,false);
        return new ClassViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        holder.classname.setText(classItems.get(position).className);
        holder.subjectname.setText(classItems.get(position).subjectName);

    }

    @Override
    public int getItemCount() {
        return classItems.size();
    }
}

