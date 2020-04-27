package com.dreamtv.app.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamtv.app.R;
import com.dreamtv.app.models.NavigationModel;
import com.dreamtv.app.utils.RecyclerviewWithFocusListener;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter extends RecyclerviewWithFocusListener.Adapter<NavigationAdapter.OriginalViewHolder> {

    private List<NavigationModel> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private OnFocusChangeListener mOnFocusChangeListener;
    NavigationAdapter.OriginalViewHolder viewHolder;
    private boolean focusAttained;

    public interface OnItemClickListener {
        void onItemClick(View view, NavigationModel obj, int position, OriginalViewHolder holder);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View view, NavigationModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public void setOnFocusChangeListener(final OnFocusChangeListener mOnFocusChangeListener) {
        this.mOnFocusChangeListener = mOnFocusChangeListener;
    }

    public NavigationAdapter(Context context, List<NavigationModel> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public NavigationAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NavigationAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_nav_view, parent, false);
        vh = new NavigationAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final OriginalViewHolder holder, final int position) {

        NavigationModel obj = items.get(position);

        if (position == 0) {
            viewHolder = holder;
            holder.cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.blue_400));
            holder.name.setTextColor(ctx.getResources().getColor(R.color.white));
        }
        holder.name.setText(obj.getTitle());
//        holder.image.setImageResource(obj.getImg());
        holder.image.setImageResource(getImageId(ctx, obj.getImg()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position, holder);

                }
            }
        });

        holder.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus && null != mOnFocusChangeListener) {
                    focusAttained = true;
                    mOnFocusChangeListener.onFocusChange(v, items.get(position), position, holder);
                }
            }
        });

        if (!focusAttained
                && position == 0) {
            holder.cardView.requestFocusFromTouch();
            holder.cardView.requestFocus();
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public CardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            cardView = v.findViewById(R.id.card_view_home);
        }

    }

    public void chanColor(NavigationAdapter.OriginalViewHolder holder, int pos) {

        if (pos != 0) {
            viewHolder.cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.white));
            viewHolder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
        }

        if (holder != null) {
            holder.cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.white));
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
        }

    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
