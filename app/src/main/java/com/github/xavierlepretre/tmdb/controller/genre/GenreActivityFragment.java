package com.github.xavierlepretre.tmdb.controller.genre;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.xavierlepretre.tmdb.model.TmdbContract;
import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.sql.movie.GenreCursor;
import com.github.xavierlepretre.tmdb.themoviedblibrary.R;

public class GenreActivityFragment extends Fragment
{
    public static final int PICK_GENRE_RESULT_OK = 1;
    private static final int GENRE_LOADER = 1;
    private static final String KEY_GENRE_ORDER = "genreOrder";

    private GenreAdapter genreAdapter;

    public GenreActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        genreAdapter = new GenreAdapter(getActivity(), null);
        View inflated = inflater.inflate(R.layout.fragment_genre, container, false);
        final RecyclerView recyclerView = (RecyclerView) inflated.findViewById(android.R.id.list);
        recyclerView.setAdapter(genreAdapter);
        final GestureDetector gestureDetector = new GestureDetector(getActivity(),
                new SimpleOnGestureListener()
                {
                    @Override public boolean onSingleTapUp(MotionEvent e)
                    {
                        return true;
                    }
                });
        recyclerView.addOnItemTouchListener(new OnItemTouchListener()
        {
            @Override public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
            {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && gestureDetector.onTouchEvent(e))
                {
                    onChildViewClicked(rv, childView);
                }
                return false;
            }

            @Override public void onTouchEvent(RecyclerView rv, MotionEvent e)
            {
            }

            @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
            {
            }
        });
        return inflated;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Bundle genreBundle = new Bundle();
        genreBundle.putString(KEY_GENRE_ORDER, GenreContract.COLUMN_NAME + " ASC");
        getLoaderManager().initLoader(
                GENRE_LOADER,
                genreBundle,
                new GenreLoaderCallback());
    }

    private class GenreLoaderCallback implements LoaderCallbacks
    {
        @Override public Loader onCreateLoader(int id, Bundle args)
        {
            if (id == GENRE_LOADER)
            {
                return new CursorLoader(
                        getActivity(),
                        GenreEntity.CONTENT_URI,
                        null, null, null,
                        args.getString(KEY_GENRE_ORDER));
            }
            throw new IllegalArgumentException("Unknown loader id: " + id);
        }

        @Override public void onLoadFinished(Loader loader, Object data)
        {
            ((Cursor) data).setNotificationUri(
                    getActivity().getContentResolver(),
                    GenreEntity.CONTENT_URI);
            genreAdapter.swapCursor(new GenreCursor((Cursor) data));
        }

        @Override public void onLoaderReset(Loader loader)
        {
            genreAdapter.swapCursor(null);
        }
    }

    private void onChildViewClicked(@NonNull RecyclerView rv, @NonNull View childView)
    {
        int position = rv.getChildLayoutPosition(childView);
        if (genreAdapter.getCursor().moveToPosition(position))
        {
            GenreId picked = genreAdapter.getCursor().getGenre().getId();
            if (getActivity().getCallingActivity() != null
                    && picked != null)
            {
                Intent intent = new Intent();
                intent.setData(TmdbContract.GenreEntity.buildUri(picked));
                getActivity().setResult(PICK_GENRE_RESULT_OK, intent);
                getActivity().supportFinishAfterTransition();
            }
        }
    }
}
