package com.dreamtv.app;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.dreamtv.app.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.dreamtv.app.adapters.CommonGridAdapter;
import com.dreamtv.app.models.CommonModels;
import com.dreamtv.app.utils.ApiResources;
import com.dreamtv.app.utils.SpacingItemDecoration;
import com.dreamtv.app.utils.ToastMsg;
import com.dreamtv.app.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private String query = "";

    private TextView tvTitle;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private CommonGridAdapter mAdapter;
    private List<CommonModels> list = new ArrayList<>();

    private ApiResources apiResources;

    private String URL = null;
    private boolean isLoading = false;
    private ProgressBar progressBar;
    private int pageCount = 1;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        query = getIntent().getStringExtra("q");

        tvTitle = findViewById(R.id.title);

        tvTitle.setText("Showing Result for : " + query);


        progressBar = findViewById(R.id.item_progress_bar);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();


        URL = new ApiResources().getSearchUrl() + "&&q=" + query + "&&page=";

        coordinatorLayout = findViewById(R.id.coordinator_lyt);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 12), true));
        recyclerView.setHasFixedSize(true);
        mAdapter = new CommonGridAdapter(this, list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !isLoading) {

                    pageCount = pageCount + 1;
                    isLoading = true;

                    progressBar.setVisibility(View.VISIBLE);

                    getData(URL, pageCount);
                }
            }
        });


        getData(URL, pageCount);

    }


    private void getData(String url, int pageNum) {

        String fullUrl = url + String.valueOf(pageNum);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                isLoading = false;
                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if (String.valueOf(response).length() < 50 && pageCount == 1) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("title"));

                        if (jsonObject.getString("is_tvseries").equals("0")) {
                            models.setVideoType("movie");
                        } else {
                            models.setVideoType("tvseries");
                        }
                        models.setId(jsonObject.getString("videos_id"));
                        list.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                new ToastMsg(SearchActivity.this).toastIconError("unable to fetch data");
                if (pageCount == 1) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }


            }
        });
        Volley.newRequestQueue(SearchActivity.this).add(jsonArrayRequest);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
