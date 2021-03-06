package com.example.vod;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.amazonaws.amplify.generated.graphql.ListVideosQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class HomeActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    private ArrayList<ListVideosQuery.Item> mVideos;
    private final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyAdapter.RecyclerViewClickListener mListener = new MyAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.i(TAG, mVideos.get(position).id());
                String title = mVideos.get(position).title();
                String genre = mVideos.get(position).genre();
                String hlsUrl = mVideos.get(position).hlsUrl();
                Intent playVideoIntent = new Intent(HomeActivity.this, VideoPlayerActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_TITLE", title);
                extras.putString("EXTRA_GENRE", genre);
                extras.putString("EXTRA_URL", hlsUrl);
                playVideoIntent.putExtras(extras);
                startActivity(playVideoIntent);
            }
        };

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, mListener);
        mRecyclerView.setAdapter(mAdapter);

        ClientFactory.init(this);

        FloatingActionButton btnAddPet = findViewById(R.id.btn_addPet);
        btnAddPet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent addPetIntent = new Intent(HomeActivity.this, UploadVideoActivity.class);
                HomeActivity.this.startActivity(addPetIntent);
            }
        });
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_bar);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    break;
                case R.id.action_profile:
                    Intent a = new Intent(HomeActivity.this,ProfileActivity.class);
                    startActivity(a);
                    overridePendingTransition(0,0);
                    break;
                case R.id.action_activity3:
                    Intent b = new Intent(HomeActivity.this,Activity3.class);
                    startActivity(b);
                    overridePendingTransition(0,0);
                    break;
            }
            return true;
        });
        MenuItem item = navigation.getMenu().findItem(R.id.action_home);
        item.setChecked(true);


    }
    @Override
    public void onResume() {
        super.onResume();

        // Query list data when we return to the screen
        query();
    }

    public void query(){
        ClientFactory.appSyncClient().query(ListVideosQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    private GraphQLCall.Callback<ListVideosQuery.Data> queryCallback = new GraphQLCall.Callback<ListVideosQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListVideosQuery.Data> response) {

            mVideos = new ArrayList<>(response.data().listVideos().items());

            Log.i(TAG, "Retrieved list items: " + mVideos.toString());

            runOnUiThread(() -> {
                mAdapter.setItems(mVideos);
                mAdapter.notifyDataSetChanged();
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };

/*
    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.action_home;
    }
*/

}
