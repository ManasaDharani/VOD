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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    private ArrayList<ListVideosQuery.Item> mVideos;
    private final String TAG = MainActivity.class.getSimpleName();



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
                Intent addPetIntent = new Intent(MainActivity.this, UploadVideoActivity.class);
                MainActivity.this.startActivity(addPetIntent);
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setItems(mVideos);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };

}
