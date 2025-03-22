package org.axel.autohakusovellus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    private ArrayList<CarData> carDataList;

    public InfoAdapter(ArrayList<CarData> carDataList) {
        this.carDataList = carDataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView typeTextView;
        public TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.carType);
            amountTextView = itemView.findViewById(R.id.carAmount);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarData carData = carDataList.get(position);
        holder.typeTextView.setText(carData.getType());
        holder.amountTextView.setText(String.valueOf(carData.getAmount()));
    }

    @Override
    public int getItemCount() {
        return carDataList.size();
    }
}
