package com.dreamtv.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dreamtv.app.adapters.NavigationAdapter;
import com.dreamtv.app.fragments.LiveTvFragment;
import com.dreamtv.app.fragments.MoviesFragment;
import com.dreamtv.app.fragments.TvSeriesFragment;
import com.dreamtv.app.models.NavigationModel;
import com.dreamtv.app.nav_fragments.CountryFragment;
import com.dreamtv.app.nav_fragments.FavoriteFragment;
import com.dreamtv.app.nav_fragments.GenreFragment;
import com.dreamtv.app.nav_fragments.MainHomeFragment;
import com.dreamtv.app.utils.GridLayoutManager;
import com.dreamtv.app.utils.RecyclerviewWithFocusListener;
import com.dreamtv.app.utils.SpacingItemDecoration;
import com.dreamtv.app.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    private RecyclerviewWithFocusListener recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list = new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;

    public static final int RECVIEW1 = 1;
    private static int currFocus = 0;
    private int currentFocusedItemPosition;
    private int selectedItemInKeyEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        //----navDrawer------------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView.setNavigationItemSelectedListener(this);


        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        navItemImage = getResources().getStringArray(R.array.nav_item_image);
        navItemImage2 = getResources().getStringArray(R.array.nav_item_image_2);


        navItemName2 = getResources().getStringArray(R.array.nav_item_name_2);


        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
        recyclerView.setHasFixedSize(true);


        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        boolean status = prefs.getBoolean("status", false);

        if (status) {
            for (int i = 0; i < navItemName.length; i++) {
                NavigationModel models = new NavigationModel(navItemImage[i], navItemName[i]);
                list.add(models);
            }
        } else {
            for (int i = 0; i < navItemName2.length; i++) {
                NavigationModel models = new NavigationModel(navItemImage2[i], navItemName2[i]);
                list.add(models);
            }
        }


        //set data and list adapter
        mAdapter = new NavigationAdapter(this, list);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setOnDispatchKeyEventListener(new RecyclerviewWithFocusListener.OnDispatchKeyEventListener() {
            @Override
            public void onDispatchKeyEventListener(int keyCode) {
                int totalChildCount = recyclerView.getChildCount();
                /**
                 * Here we check, if the recyclerview is in-focus or not.
                 * If the recyclerview is not in-focus, then we make the
                 * first item in the recycler view in-focus
                 */
                if (currFocus != RECVIEW1) {
                    currFocus = RECVIEW1;
                    currentFocusedItemPosition = 0;
                    recyclerView.getChildAt(currentFocusedItemPosition).requestFocus();
                } else {
                    if (totalChildCount > 0) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                Toast.makeText(MainActivity.this, "D-PAD DOWN >> \nCurrent Position >" + currentFocusedItemPosition
                                        + "\nselectedItem Position in KeyEventDispatcher> " + selectedItemInKeyEventListener, Toast.LENGTH_SHORT).show();
                                if (selectedItemInKeyEventListener == currentFocusedItemPosition) {
                                    selectedItemInKeyEventListener = currentFocusedItemPosition + 2;
                                    if (selectedItemInKeyEventListener < totalChildCount) {
                                        System.out.println("TRIGGERED >> D-PAD DOWN, position > " + selectedItemInKeyEventListener);
                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocusFromTouch();
                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocus();
                                    }
                                    Toast.makeText(MainActivity.this, "D-PAD DOWN", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_UP:
                                Toast.makeText(MainActivity.this, "D-PAD UP >> \nCurrent Position >" + currentFocusedItemPosition
                                        + "\nselectedItem Position in KeyEventDispatcher > " + selectedItemInKeyEventListener, Toast.LENGTH_SHORT).show();
                                if (selectedItemInKeyEventListener == currentFocusedItemPosition) {
                                    selectedItemInKeyEventListener = currentFocusedItemPosition - 2;
                                    if (selectedItemInKeyEventListener >= 0) {
                                        System.out.println("TRIGGERED >> D-PAD UP, position > " + selectedItemInKeyEventListener);

                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocusFromTouch();
                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocus();
                                    }
                                    Toast.makeText(MainActivity.this, "D-PAD UP", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                                Toast.makeText(MainActivity.this, "D-PAD RIGHT >> \nCurrent Position >" + currentFocusedItemPosition
                                        + "\nselectedItem Position in KeyEventDispatcher > " + selectedItemInKeyEventListener, Toast.LENGTH_SHORT).show();
                                if (selectedItemInKeyEventListener == currentFocusedItemPosition) {
                                    selectedItemInKeyEventListener = currentFocusedItemPosition + 1;
                                    if (selectedItemInKeyEventListener > 0 &&
                                            (selectedItemInKeyEventListener == 1 || selectedItemInKeyEventListener % 2 != 0)) {
                                        System.out.println("TRIGGERED >> D-PAD RIGHT, position > " + selectedItemInKeyEventListener);

                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocusFromTouch();
                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocus();
                                    }
                                    Toast.makeText(MainActivity.this, "D-PAD RIGHT", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                Toast.makeText(MainActivity.this, "D-PAD LEFT >> \nCurrent Position >" + currentFocusedItemPosition
                                        + "\nselectedItem Position in KeyEventDispatcher > " + selectedItemInKeyEventListener, Toast.LENGTH_SHORT).show();

                                if (selectedItemInKeyEventListener == currentFocusedItemPosition) {
                                    selectedItemInKeyEventListener = currentFocusedItemPosition - 1;
                                    if (selectedItemInKeyEventListener == 0 || selectedItemInKeyEventListener % 2 == 0) {
                                        System.out.println("TRIGGERED >> D-PAD LEFT, position > " + selectedItemInKeyEventListener);

                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocusFromTouch();
                                        recyclerView.getChildAt(selectedItemInKeyEventListener).requestFocus();
                                    }
                                    Toast.makeText(MainActivity.this, "D-PAD LEFT", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                                Toast.makeText(MainActivity.this, "D-PAD CLICK >> \nCurrent Position >" + currentFocusedItemPosition
                                        + "\nselectedItem Position in KeyEventDispatcher > " + selectedItemInKeyEventListener, Toast.LENGTH_SHORT).show();

                                System.out.println("TRIGGERED >> D-PAD CLICK, position > " + currentFocusedItemPosition);

                                recyclerView.getChildAt(currentFocusedItemPosition).requestFocusFromTouch();
                                recyclerView.getChildAt(currentFocusedItemPosition).requestFocus();
                                recyclerView.getChildAt(currentFocusedItemPosition).callOnClick();
                                Toast.makeText(MainActivity.this, "D-PAD CLICK", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                }

            }
        });


        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        mAdapter.setOnFocusChangeListener(new NavigationAdapter.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {
                currentFocusedItemPosition = position;
                Toast.makeText(MainActivity.this, "TRIGGERED >> onFocusChange, position >  \nCurrent Position >" + currentFocusedItemPosition
                        + "\nselectedItem Position in KeyEventDispatcher > " + selectedItemInKeyEventListener, Toast.LENGTH_SHORT).show();

                mAdapter.chanColor(viewHolder[0], position);
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                holder.name.setTextColor(getResources().getColor(R.color.grey_500));
                viewHolder[0] = holder;
            }
        });
        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {

                //----action for click items nav---------------------

                if (obj.getTitle().equals("Home")) {
                    loadFragment(new MainHomeFragment());
                } else if (obj.getTitle().equals("Settings")) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (obj.getTitle().equals("Movies")) {
                    loadFragment(new MoviesFragment());
                } else if (obj.getTitle().equals("Live TV")) {
                    loadFragment(new LiveTvFragment());
                } else if (obj.getTitle().equals("TV Series")) {
                    loadFragment(new TvSeriesFragment());
                } else if (obj.getTitle().equals("Country")) {
                    loadFragment(new CountryFragment());
                } else if (obj.getTitle().equals("Genre")) {
                    loadFragment(new GenreFragment());
                } else if (obj.getTitle().equals("Login")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if (obj.getTitle().equals("Profile")) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (obj.getTitle().equals("Sign Out")) {


                    new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure to logout ?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                    editor.putBoolean("status", false);
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();


                } else if (obj.getTitle().equals("Favorite")) {
                    loadFragment(new FavoriteFragment());
                }

                //----behaviour of bg nav items-----------------
                if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")) {
                    mAdapter.chanColor(viewHolder[0], position);
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    holder.name.setTextColor(getResources().getColor(R.color.grey_500));
                    viewHolder[0] = holder;
                }

                mDrawerLayout.closeDrawers();
            }
        });
        //----external method call--------------
        loadFragment(new MainHomeFragment());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }


    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra("q", s);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {

            new AlertDialog.Builder(MainActivity.this).setMessage("Do you want to exit ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();

        }
    }

    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }
}