package com.example.redditapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.redditapp.model.Feed;
import com.example.redditapp.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://www.reddit.com/r/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed();

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                List<Entry> entries = response.body().getEntries();

                ArrayList<Post> posts = new ArrayList<>();

                for (int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML("<a href=", entries.get(i).getContent());
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML("<img src=", entries.get(i).getContent());
                    List<String> thumbnails = extractXML2.start();

                    // Add thumbnail if it exists
                    if (!thumbnails.isEmpty()) {
                        postContent.add(thumbnails.get(0));
                    } else {
                        postContent.add(null);
                        Log.d(TAG, "onResponse: No thumbnail found for entry " + i);
                    }

                    int lastIndex = postContent.size() - 1;

                    posts.add(new Post(
                            entries.get(i).getTitle(),
                            entries.get(i).getAuthor().getName(),
                            entries.get(i).getUpdated(),
                            postContent.get(0),         // Post URL
                            postContent.get(lastIndex)  // Thumbnail URL
                    ));
                }


                for(int j = 0; j < posts.size(); j++) {
                    Log.d(TAG, "onResponse: \n" +
                            "PostURL: " + posts.get(j).getPostURL() + "\n " +
                            "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n " +
                            "Title: " + posts.get(j).getTitle() + "\n " +
                           "Author: " + posts.get(j).getAuthor() + "\n " +
                            "updated: " + posts.get(j).getDate_updated() + "\n ");
                }

                ListView listView = (ListView) findViewById(R.id.listView);
                com.example.redditapp.CustomListAdapter customListAdapter = new com.example.redditapp.CustomListAdapter(MainActivity.this, R.layout.card_layout_main, posts);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable throwable) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS" + throwable.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}