package com.example.jatin.wi_bird;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Created by Jatin Mittal on 02-Jun-16.
 */
public class VivzAdapter extends RecyclerView.Adapter<VivzAdapter.MyViewHolder> {
    List<Information> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private int selectedPos = 0;

    public VivzAdapter(Context context, List<Information> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information current = data.get(position);
        holder.itemView.setSelected(selectedPos == position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i;
            /*notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);*/

            int pos = getPosition();
            if (pos == 0) {

                i = new Intent(context, AboutUsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            } else if (pos == 1) {
                i = new Intent(context, SensorActivity.class);
                //i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //i.setClassName(context,"com.example.jatin.wi_bird.SensorActivity");
                context.startActivity(i);
            } else if(pos == 2){
                i = new Intent(context, VelocityActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        }
    }
}


