package com.wingshield.technologies.taskbyarun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wingshield.technologies.taskbyarun.R;
import com.wingshield.technologies.taskbyarun.model.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Arun Kumar on 15/07/2021.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private final Context context;
    private final List<Data> dataList;


    public DataAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(dataList.get(position).getFile()).placeholder(R.mipmap.ic_launcher).into(holder.file);

        holder.fileName.setText(dataList.get(position).getFileName());
        holder.fileDate.setText(convertTime(dataList.get(position).getDateTime()));
       // holder.fileSize.setText(dataList.get(position).getFileName());
        holder.fileType.setText(dataList.get(position).getFiletype());
        holder.noOfPages.setText("Pgs : "+dataList.get(position).getNoOfPages());
    }


    public String convertTime(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return formatter.format(new Date(Long.parseLong(time)));
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private TextView fileType, fileDate,fileSize,noOfPages;
        private ImageView file;
        private RelativeLayout rl_layout;

        MyViewHolder(View view) {
            super(view);
            file = view.findViewById(R.id.file);
            fileName = view.findViewById(R.id.fileName);
            fileDate = view.findViewById(R.id.fileDate);
            fileSize = view.findViewById(R.id.fileSize);
            fileType = view.findViewById(R.id.fileType);
            noOfPages = view.findViewById(R.id.noOfPages);


        }
    }
}
