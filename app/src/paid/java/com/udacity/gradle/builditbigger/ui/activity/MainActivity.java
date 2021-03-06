package com.udacity.gradle.builditbigger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.amrendra.displaylibrary.JokeDisplayActivity;
import com.squareup.otto.Subscribe;
import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.bus.BusProvider;
import com.udacity.gradle.builditbigger.event.JokeLoadedEvent;
import com.udacity.gradle.builditbigger.logger.Debug;
import com.udacity.gradle.builditbigger.task.FetchJokeTask;
import com.udacity.gradle.builditbigger.utils.JokeConstants;


public class MainActivity extends ActionBarActivity {
    LinearLayout mProgressBar;
    boolean jokeRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            jokeRequested = savedInstanceState.getBoolean(JokeConstants.JOKE_REQUESTED,
                    false);
        }
        setContentView(R.layout.activity_main);
        mProgressBar = (LinearLayout) findViewById(R.id.progressLayout);
        if (jokeRequested) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        BusProvider.getInstance().register(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        Debug.i("Joke Clicked", false);
        if (!jokeRequested) {
            mProgressBar.setVisibility(View.VISIBLE);
            jokeRequested = true;
            new FetchJokeTask().execute();
        } else {
            Debug.showToastShort(getString(R.string.please_wait), this, true);
        }
    }


    @Subscribe
    public void jokeLoaded(JokeLoadedEvent event) {
        jokeRequested = false;
        String joke = event.getJoke();
        Debug.i("Going to show the Joke Loaded : " + joke, false);
        mProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, JokeDisplayActivity.class);
        intent.putExtra(JokeDisplayActivity.JOKE_DISPLAY_INTENT, joke);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(JokeConstants.JOKE_REQUESTED, jokeRequested);
        super.onSaveInstanceState(savedInstanceState);
    }
}
