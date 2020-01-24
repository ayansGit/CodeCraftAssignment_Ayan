package com.ayan.architectureapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayan.architectureapp.R;
import com.ayan.architectureapp.models.restaurant.Restaurant;
import com.ayan.architectureapp.utils.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {


    private List<Restaurant> restaurantList = new ArrayList<>();

    private OnRestaurantItemClickListener onRestaurantItemClickListener;

    public RestaurantListAdapter(OnRestaurantItemClickListener onRestaurantItemClickListener) {
        this.onRestaurantItemClickListener = onRestaurantItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvRestaurantName.setText(restaurantList.get(position).getRestaurantName());
        holder.tvRestaurantVicinity.setText(restaurantList.get(position).getVicinity());
        new ImageDownloader(holder.ivRestaurantImage).loadImage(restaurantList.get(position).getImage());

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRestaurantItemClickListener.setOnRestaurantItemClickListener(restaurantList.get(position));
            }
        });

    }


    @Override
    public int getItemCount() {
        Log.d("ADAPTER",String.valueOf(restaurantList.size()));
        return restaurantList.size();
    }

    public void setRestaurant(List<Restaurant> restaurantList){
        this.restaurantList = restaurantList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRestaurantImage;
        TextView tvRestaurantName;
        TextView tvRestaurantVicinity;
        TextView tvDistance;
        LinearLayout llItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llItem = itemView.findViewById(R.id.llItem);
            ivRestaurantImage = itemView.findViewById(R.id.ivRestaurantImage);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvRestaurantVicinity = itemView.findViewById(R.id.tvRestaurantVicinity);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }

    public interface OnRestaurantItemClickListener{
        void setOnRestaurantItemClickListener(Restaurant restaurant);
    }
}
