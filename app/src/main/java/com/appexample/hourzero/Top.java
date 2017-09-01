package com.appexample.hourzero;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Top extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Main2Activity> newsFeed = new ArrayList<>();
    PullRefreshLayout layout;
    private RecyclerView recyclerView;
    private Adapter mAdapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        newsFeed=new ArrayList<>();
        mAdapter = new Adapter(this, newsFeed);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter.notifyDataSetChanged();
        //engine();
        LoadCache();
//        addClickListener();


// listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                engine();
            }
        });
// refresh complete

            recyclerView.setOnScrollListener(new EndlessScrolling(linearLayoutManager) {
                @Override
                public void onLoadMore(int current_page) {
                    // do something...
                    Toast.makeText(Top.this, "Loading more...", Toast.LENGTH_SHORT).show();
                    engine();
                }
            });
    }


    private void engine() {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest myRequest = new JsonObjectRequest(Request.Method.GET,
                "https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=5f8f27265217470e993530b14532d45f", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(Top.this, "Updated", Toast.LENGTH_SHORT).show();
                            JSONArray newsItems = response.getJSONArray("articles");
                            newsFeed = new ArrayList<>();
                            for (int i = 0; i < newsItems.length(); i++) {
                                JSONObject temp = newsItems.getJSONObject(i);

                                String author = temp.getString("author");
                                String title = temp.getString("title");
                                String description = temp.getString("description");
                                String url = temp.getString("url");
                                String imgurl = temp.getString("urlToImage");
                                String published = temp.getString("publishedAt");

                                newsFeed.add(new Main2Activity(author, title, description, url, imgurl, published));
                                //adapter.notifyDataSetChanged();
                            }
                            layout.setRefreshing(false);

                            mAdapter = new Adapter(Top.this, newsFeed);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setLayoutManager(linearLayoutManager);
                        } catch (JSONException e) {
                            Log.i("myTag", e.toString());
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("myTag", error.toString());
                        LoadCache();
                    }
                });
        myRequest.setShouldCache(true);
        AppController.getInstance().addToRequestQueue(myRequest);
//        myRequest.setRetryPolicy(new DefaultRetryPolicy(
//                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        queue.add(myRequest);


    }

    private void LoadCache() {
        String url_1 = "https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=5f8f27265217470e993530b14532d45f";
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url_1);
        if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                Toast.makeText(Top.this, "Came here \n"+data, Toast.LENGTH_SHORT).show();
                // handle data, like converting it to xml, json, bitmap etc.,
                JSONObject response=new JSONObject(data);
                JSONArray newsItems = response.getJSONArray("articles");
                newsFeed = new ArrayList<>();
                for (int i = 0; i < newsItems.length(); i++) {
                    JSONObject temp = newsItems.getJSONObject(i);

                    String author = temp.getString("author");
                    String title = temp.getString("title");
                    String description = temp.getString("description");
                    String url = temp.getString("url");
                    String imgurl = temp.getString("urlToImage");
                    String published = temp.getString("publishedAt");

                    newsFeed.add(new Main2Activity(author, title, description, url, imgurl, published));
                }
                mAdapter = new Adapter(this, newsFeed);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Cached response doesn't exists. Make network call here
            Toast.makeText(Top.this, "Wait a moment please", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent myIntent = new Intent(Top.this, About.class);
            Top.this.startActivity(myIntent);
            return true;
        }

        if (id == R.id.refresh) {
            Toast.makeText(Top.this, "Updating...", Toast.LENGTH_SHORT).show();

            layout.setRefreshing(true);
            engine();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Home) {
            Intent myIntent = new Intent(Top.this, MainActivity.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Top) {
            Intent myIntent = new Intent(Top.this, Top.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Gaming) {
            Intent myIntent = new Intent(Top.this, Gaming.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Education) {
            Intent myIntent = new Intent(Top.this, Education.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Entertainment) {
            Intent myIntent = new Intent(Top.this, Entertainment.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Music) {
            Intent myIntent = new Intent(Top.this, Music.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.world) {
            Intent myIntent = new Intent(Top.this, World.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Tech) {
            Intent myIntent = new Intent(Top.this, Tech.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Sports) {
            Intent myIntent = new Intent(Top.this, Sports.class);
            Top.this.startActivity(myIntent);

        } else if (id == R.id.Finance) {
            Intent myIntent = new Intent(Top.this, Finance.class);
            Top.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}



