package com.github.xavierlepretre.tmdb.controller.genre;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.xavierlepretre.tmdb.controller.CursorRecyclerViewAdapter;
import com.github.xavierlepretre.tmdb.model.movie.GenreCursor;

public class GenreAdapter extends CursorRecyclerViewAdapter<GenreItemViewHolder>
{
    public GenreAdapter(Context context, GenreCursor cursor)
    {
        super(context, cursor);
    }

    @Override public GenreCursor getCursor()
    {
        return (GenreCursor) super.getCursor();
    }

    @Override public GenreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflated = LayoutInflater.from(getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new GenreItemViewHolder(inflated);
    }

    @Override public void onBindViewHolder(GenreItemViewHolder viewHolder, Cursor cursor)
    {
        viewHolder.bind(((GenreCursor) cursor).getGenre());
    }
}
