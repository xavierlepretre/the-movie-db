package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.Nullable;

public class MovieGenre
{
    @Nullable private final MovieId movieId;
    @Nullable private final GenreId genreId;

    public MovieGenre(@Nullable MovieId movieId, @Nullable GenreId genreId)
    {
        this.movieId = movieId;
        this.genreId = genreId;
    }

    @Nullable public MovieId getMovieId()
    {
        return movieId;
    }

    @Nullable public GenreId getGenreId()
    {
        return genreId;
    }

    @Override public final int hashCode()
    {
        return (movieId == null ? 0 : movieId.hashCode())
                ^ (genreId == null ? 0 : genreId.hashCode());
    }

    @Override public final boolean equals(Object o)
    {
        return o instanceof MovieGenre
                && (movieId == null ? ((MovieGenre) o).movieId == null : movieId.equals(((MovieGenre) o).movieId))
                && (genreId == null ? ((MovieGenre) o).genreId == null : genreId.equals(((MovieGenre) o).genreId));
    }
}
