package com.github.xavierlepretre.tmdb.themoviedblibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.github.xavierlepretre.tmdb.controller.genre.GenreActivityFragment;
import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;

public class MainActivity extends AppCompatActivity
{
    private static final int GENRE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.btn_genre).setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                requestGenre();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case GENRE_REQUEST_CODE:
                if (resultCode == GenreActivityFragment.PICK_GENRE_RESULT_OK
                        && data != null)
                {
                    Uri uri = data.getData();
                    if (uri != null)
                    {
                        Snackbar.make(
                                getWindow().getDecorView(),
                                uri.toString(),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
                break;
        }
    }

    protected void requestGenre()
    {
//        startActivityForResult(
//                new Intent(this, GenreActivity.class),
//                GENRE_REQUEST_CODE);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(GenreEntity.CONTENT_ITEM_TYPE);
        startActivityForResult(
                intent,
                GENRE_REQUEST_CODE);
    }

}
