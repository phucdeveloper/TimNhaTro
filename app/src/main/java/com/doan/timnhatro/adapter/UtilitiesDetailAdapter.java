package com.doan.timnhatro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;
import com.doan.timnhatro.model.Utilities;

import java.util.ArrayList;

public class UtilitiesDetailAdapter extends RecyclerView.Adapter<UtilitiesDetailAdapter.ViewHolder> {

    ArrayList<Utilities> arrayList;
    Context context;

    public UtilitiesDetailAdapter(ArrayList<Utilities> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utilities_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgIcon.setImageResource(arrayList.get(position).getIconUtilities());
        holder.txtName.setText(arrayList.get(position).getNameUtilities());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgIcon;
        TextView txtName;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.item_imageview_icon_utilities);
            txtName = itemView.findViewById(R.id.item_textview_name_utilities);
            linearLayout = itemView.findViewById(R.id.layout);
        }
    }
}
