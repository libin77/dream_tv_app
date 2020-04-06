package com.dreamtv.app.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamtv.app.DetailsActivity;
import com.dreamtv.app.models.CommonModels;
import com.dreamtv.app.R;

import java.util.ArrayList;
import java.util.List;

public class ServerApater extends RecyclerView.Adapter<ServerApater.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;

    private ServerApater.OnItemClickListener mOnItemClickListener;

    private ServerApater.OriginalViewHolder viewHolder;


    public interface OnItemClickListener {
        void onItemClick(View view, CommonModels obj, int position, OriginalViewHolder holder);
    }

    public void setOnItemClickListener(ServerApater.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public ServerApater(Context context, List<CommonModels> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public ServerApater.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ServerApater.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_server, parent, false);
        vh = new ServerApater.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ServerApater.OriginalViewHolder holder, final int position) {

        CommonModels obj = items.get(position);
        holder.name.setText(obj.getTitle());


        if (position == 0) {
            viewHolder = holder;
            new DetailsActivity().iniMoviePlayer(obj.getStremURL(), obj.getServerType(), ctx);
            holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position, holder);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public CardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            cardView = v.findViewById(R.id.card_view_home);
        }
    }

    public void chanColor(ServerApater.OriginalViewHolder holder, int pos) {

        if (pos != 0) {
            viewHolder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
        }

        if (holder != null) {
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
        }


    }

}