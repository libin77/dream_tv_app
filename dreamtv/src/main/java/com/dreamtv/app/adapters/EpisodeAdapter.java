package com.dreamtv.app.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamtv.app.models.CommonModels;
import com.dreamtv.app.models.EpiModel;
import com.dreamtv.app.R;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;


    public EpisodeAdapter(Context context, List<CommonModels> items, List<CommonModels> listEpi) {
        this.items = items;
        ctx = context;

    }


    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode, parent, false);
        vh = new EpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EpisodeAdapter.OriginalViewHolder holder, final int position) {

        CommonModels obj = items.get(position);
        holder.name.setText("Season : " + obj.getTitle());

        List<EpiModel> listEpi;
        listEpi = items.get(0).getEpiModels();


        DirectorApater directorApater = new DirectorApater(ctx, listEpi, obj.getTitle());
        Log.e("List", String.valueOf(obj.getTitle()));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(directorApater);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public RecyclerView recyclerView;


        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            recyclerView = v.findViewById(R.id.recyclerView);


        }
    }

}