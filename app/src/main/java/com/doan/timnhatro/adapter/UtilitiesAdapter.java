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

public class UtilitiesAdapter extends RecyclerView.Adapter<UtilitiesAdapter.ViewHolder> {

    ArrayList<Utilities> arrayList;
    Context context;
    OnItemUtilitiesClickListener onItemUtilitiesClickListener;
    StringBuilder stringBuilder = new StringBuilder();
    ArrayList<String> listString = new ArrayList<>();

    public UtilitiesAdapter(ArrayList<Utilities> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public void setOnItemUtilitiesClickListener(OnItemUtilitiesClickListener onItemUtilitiesClickListener) {
        this.onItemUtilitiesClickListener = onItemUtilitiesClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utilities, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgIcon.setImageResource(arrayList.get(position).getIconUtilities());
        holder.txtName.setText(arrayList.get(position).getNameUtilities());

        if (arrayList.get(position).isChecked()){
            holder.linearLayout.setBackgroundResource(R.drawable.custom_background_utilities_checked);
            holder.txtName.setTextColor(Color.BLUE);
        }
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

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (!arrayList.get(position).isChecked()){
                    arrayList.get(position).setChecked(true);
                }
                else{
                    arrayList.get(position).setChecked(false);
                }

                if (arrayList.get(position).isChecked()){
                    listString.add(arrayList.get(position).getNameUtilities());
                    linearLayout.setBackgroundResource(R.drawable.custom_background_utilities_checked);
                    txtName.setTextColor(Color.BLUE);
                }
                else{
                    listString.remove(arrayList.get(position).getNameUtilities());
                    linearLayout.setBackgroundResource(R.drawable.custom_background_utilities_unchecked);
                    txtName.setTextColor(Color.DKGRAY);
                }

                if (onItemUtilitiesClickListener != null){
                    onItemUtilitiesClickListener.onItemClick(listString);
                }
            });
        }
    }

    public interface OnItemUtilitiesClickListener{
        void onItemClick(ArrayList<String> stringArrayList);
    }
}
