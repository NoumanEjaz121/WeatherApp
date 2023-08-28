package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.viewHolder> {
    private Context context;
    private ArrayList <WeatherRVModel> WeatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        WeatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        WeatherRVModel model = WeatherRVModelArrayList.get(position);
        holder.temperatureTV.setText(model.getTemperature()+"°c");
        holder.windTV.setText(model.getWindSpeed()+ "km/h");
        Picasso.get().load("http:".concat(model.getIconUrl())).into(holder.conditionTV);
        SimpleDateFormat input = new SimpleDateFormat("yyyy_mm_dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm am");

        try{
            Date t = input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {

        return WeatherRVModelArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView windTV,temperatureTV,timeTV;
        private ImageView conditionTV;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            windTV = itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionTV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}

