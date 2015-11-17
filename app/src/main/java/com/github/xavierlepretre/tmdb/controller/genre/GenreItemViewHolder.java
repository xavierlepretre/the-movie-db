package com.github.xavierlepretre.tmdb.controller.genre;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.xavierlepretre.tmdb.model.movie.Genre;

public class GenreItemViewHolder extends RecyclerView.ViewHolder
{
    @NonNull private final TextView nameView;

    public GenreItemViewHolder(View itemView)
    {
        super(itemView);
        nameView = (TextView) itemView.findViewById(android.R.id.text1);
    }

    public void bind(@Nullable Genre genre)
    {
        nameView.setText(genre == null ? "Null" : genre.getName());
    }
}
