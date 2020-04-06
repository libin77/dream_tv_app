package com.dreamtv.app.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dreamtv.app.DetailsActivity;
import com.dreamtv.app.ItemMovieActivity;
import com.dreamtv.app.ItemSeriesActivity;
import com.dreamtv.app.ItemTVActivity;
import com.dreamtv.app.adapters.GenreHomeAdapter;
import com.dreamtv.app.adapters.HomePageAdapter;
import com.dreamtv.app.adapters.LiveTvHomeAdapter;
import com.dreamtv.app.models.CommonModels;
import com.dreamtv.app.models.GenreModel;
import com.dreamtv.app.utils.ApiResources;
import com.dreamtv.app.utils.NetworkInst;
import com.dreamtv.app.utils.VolleySingleton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.dreamtv.app.R;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    CirclePageIndicator indicator;

    private List<CommonModels> listSlider = new ArrayList<>();

    private Timer timer;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewMovie, recyclerViewTv, recyclerViewTvSeries, recyclerViewGenre;
    private HomePageAdapter adapterMovie, adapterSeries;
    private LiveTvHomeAdapter adapterTv;
    private List<CommonModels> listMovie = new ArrayList<>();
    private List<CommonModels> listTv = new ArrayList<>();
    private List<CommonModels> listSeries = new ArrayList<>();
    private ApiResources apiResources;
    private Button btnMoreMovie, btnMoreTv, btnMoreSeries;

    private int checkPass = 0;

    private SliderAdapter sliderAdapter;

    private VolleySingleton singleton;
    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;

    private AdView adView;


    private List<GenreModel> listGenre = new ArrayList<>();
//    private List<CommonModels> listGenreMovie = new ArrayList<>();

    private GenreHomeAdapter genreHomeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.app_title));
        apiResources = new ApiResources();

        singleton = new VolleySingleton(getActivity());

        adView = view.findViewById(R.id.adView);
        btnMoreSeries = view.findViewById(R.id.btn_more_series);
        btnMoreTv = view.findViewById(R.id.btn_more_tv);
        btnMoreMovie = view.findViewById(R.id.btn_more_movie);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        tvNoItem = view.findViewById(R.id.tv_noitem);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        scrollView = view.findViewById(R.id.scrollView);


        sliderAdapter = new SliderAdapter(getActivity(), listSlider);
        viewPager.setAdapter(sliderAdapter);
        indicator.setViewPager(viewPager);

        //----init timer slider--------------------
        timer = new Timer();


        //----btn click-------------
        btnClick();


        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTv.setHasFixedSize(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getContext(), listTv);
        recyclerViewTv.setAdapter(adapterTv);


        //----movie's recycler view-----------------
        recyclerViewMovie = view.findViewById(R.id.recyclerView);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie.setHasFixedSize(true);
        recyclerViewMovie.setNestedScrollingEnabled(false);
        adapterMovie = new HomePageAdapter(getContext(), listMovie);
        recyclerViewMovie.setAdapter(adapterMovie);

        //----series's recycler view-----------------
        recyclerViewTvSeries = view.findViewById(R.id.recyclerViewTvSeries);
        recyclerViewTvSeries.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTvSeries.setHasFixedSize(true);
        recyclerViewTvSeries.setNestedScrollingEnabled(false);
        adapterSeries = new HomePageAdapter(getContext(), listSeries);
        recyclerViewTvSeries.setAdapter(adapterSeries);

        //----genre's recycler view--------------------
        recyclerViewGenre = view.findViewById(R.id.recyclerView_by_genre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGenre.setHasFixedSize(true);
        recyclerViewGenre.setNestedScrollingEnabled(false);
        genreHomeAdapter = new GenreHomeAdapter(getContext(), listGenre);
        recyclerViewGenre.setAdapter(genreHomeAdapter);


        shimmerFrameLayout.startShimmer();


        if (new NetworkInst(getContext()).isNetworkAvailable()) {

            getFeaturedTV();
            getSlider(apiResources.getSlider());


        } else {
            tvNoItem.setText("No internet connection !");
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerViewMovie.removeAllViews();
                recyclerViewTv.removeAllViews();
                recyclerViewTvSeries.removeAllViews();

                listMovie.clear();
                listSeries.clear();
                listSlider.clear();
                listTv.clear();


                if (new NetworkInst(getContext()).isNetworkAvailable()) {
                    getFeaturedTV();
                    getSlider(apiResources.getSlider());
                } else {
                    tvNoItem.setText("No internet connection !");
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }

    private void btnClick() {

        btnMoreMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("url", apiResources.getGet_movie());
                intent.putExtra("title", "Movies");
                getActivity().startActivity(intent);
            }
        });
        btnMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemTVActivity.class);
                intent.putExtra("url", apiResources.getGet_live_tv());
                intent.putExtra("title", "Live TV");
                getActivity().startActivity(intent);
            }
        });
        btnMoreSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemSeriesActivity.class);
                intent.putExtra("url", apiResources.getTvSeries());
                intent.putExtra("title", "TV Series");
                getActivity().startActivity(intent);
            }
        });

    }


    private void getDataByGenre() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, new ApiResources().getGenreMovieURL(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    try {

                        JSONObject jsonObject = response.getJSONObject(i);

                        GenreModel models = new GenreModel();

                        models.setName(jsonObject.getString("name"));
                        models.setId(jsonObject.getString("genre_id"));

                        //Log.e("GENREEEEEEEEEEEEEEE",jsonObject.getString("name"));


                        JSONArray jsonArray = jsonObject.getJSONArray("videos");
                        //listGenreMovie.clear();
                        List<CommonModels> listGenreMovie = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {

                            JSONObject movieObject = jsonArray.getJSONObject(j);

                            CommonModels commonModels = new CommonModels();

                            commonModels.setId(movieObject.getString("videos_id"));
                            commonModels.setTitle(movieObject.getString("title"));
                            commonModels.setVideoType("movie");
                            commonModels.setImageUrl(movieObject.getString("poster_url"));

                            listGenreMovie.add(commonModels);

                        }


                        models.setList(listGenreMovie);

                        listGenre.add(models);
                        genreHomeAdapter.notifyDataSetChanged();
//                        Log.e("LIST 2 SIZE ::", String.valueOf(listGenreMovie.size()));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);


    }


    private void getSlider(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    coordinatorLayout.setVisibility(View.GONE);

                    if (response.getString("slider_type").equals("movie")) {

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommonModels models = new CommonModels();
                            models.setImageUrl(jsonObject.getString("poster_url"));
                            models.setTitle(jsonObject.getString("title"));
                            models.setVideoType("movie");
                            models.setId(jsonObject.getString("videos_id"));

                            listSlider.add(models);
                        }

                    } else {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommonModels models = new CommonModels();
                            models.setImageUrl(jsonObject.getString("image_link"));
                            models.setTitle(jsonObject.getString("title"));
                            models.setVideoType("image");
                            listSlider.add(models);

                        }
                    }

                    sliderAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);

            }
        });

        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);


    }


    private void getLatestSeries() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getLatestTvSeries(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("title"));
                        models.setVideoType("tvseries");
                        models.setId(jsonObject.getString("videos_id"));
                        listSeries.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterSeries.notifyDataSetChanged();
                getDataByGenre();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    private void getLatestMovie() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getLatest_movie(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("title"));
                        models.setVideoType("movie");
                        models.setId(jsonObject.getString("videos_id"));
                        listMovie.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterMovie.notifyDataSetChanged();
                getLatestSeries();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    private void getFeaturedTV() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getGet_featured_tv(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listTv.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterTv.notifyDataSetChanged();
                getLatestMovie();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    @Override
    public void onStart() {
        super.onStart();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 5000, 5000);
    }

    //----timer for auto slide------------------
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < listSlider.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }

        }
    }

    //----adapter for slider-------------
    public class SliderAdapter extends PagerAdapter {

        private Context context;
        private List<CommonModels> list = new ArrayList<>();

        public SliderAdapter(Context context, List<CommonModels> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_slider, null);

            View lyt_parent = view.findViewById(R.id.lyt_parent);

            final CommonModels models = list.get(position);

            TextView textView = view.findViewById(R.id.textView);

            textView.setText(models.getTitle());

            ImageView imageView = view.findViewById(R.id.imageview);

            Picasso.get().load(models.getImageUrl()).into(imageView);


            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);

            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (models.getVideoType().equals("movie")) {

                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("vType", models.getVideoType());
                        intent.putExtra("id", models.getId());
                        startActivity(intent);

                    } else {

                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager viewPager = (ViewPager) container;
            View view = (View) object;
            viewPager.removeView(view);
        }
    }


}
