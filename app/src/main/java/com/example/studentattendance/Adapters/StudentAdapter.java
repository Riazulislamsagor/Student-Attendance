package com.example.studentattendance.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattendance.DataHelper;
import com.example.studentattendance.R;
import com.example.studentattendance.Models.StudentItem;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

ArrayList<StudentItem> studentItem;
Context context;

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void Onclick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public StudentAdapter(Context context, ArrayList<StudentItem> studentItem, DataHelper dataHelper) {
        this.context=context;

        this.studentItem = studentItem;

    }

    public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView roll,name,status;
        CardView cardView;
        public StudentViewHolder(@NonNull View itemView ,OnItemClickListener onItemClickListener) {
            super(itemView);
         roll=   itemView.findViewById(R.id.rollId);
         name=   itemView.findViewById(R.id.nameIdId);
         status = itemView.findViewById(R.id.statusId);
         cardView=itemView.findViewById(R.id.cardviewId);
         itemView.setOnClickListener(v->onItemClickListener.Onclick(getAdapterPosition()));
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
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item,null,false);
        return new StudentViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.roll.setText(studentItem.get(position).getRoll()+"");
        holder.name.setText(studentItem.get(position).getName());
        holder.status.setText(studentItem.get(position).getStatus());
        holder.cardView.setCardBackgroundColor(getColor(position));

    }

    private int getColor(int position) {
        String status=studentItem.get(position).getStatus();
        if(status.equals("P"))
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.present)));
        else if(status.equals("A"))
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.absent)));
        else
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.normal)));


    }

    @Override
    public int getItemCount() {
        return studentItem.size();
    }
}

