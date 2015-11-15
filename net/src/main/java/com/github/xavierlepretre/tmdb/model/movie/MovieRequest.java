package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

public class MovieRequest
{
    @NonNull private final MovieId movieId;
    @NonNull private final MovieRequestParameters parameters;

    public MovieRequest(@NonNull MovieId movieId)
    {
        this(movieId, new MovieRequestParameters());
    }

    public MovieRequest(
            @NonNull MovieId movieId,
            @NonNull MovieRequestParameters parameters)
    {
        this.movieId = movieId;
        this.parameters = parameters;
    }

    @NonNull public MovieId getMovieId()
    {
        return movieId;
    }

    @NonNull public MovieRequestParameters getParameters()
    {
        return parameters;
    }

    @Override public final int hashCode()
    {
        return movieId.hashCode()
                ^ parameters.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        if (!(obj instanceof MovieRequest))
        {
            return false;
        }
        MovieRequest other = (MovieRequest) obj;
        return other.getMovieId().equals(movieId)
                && other.getParameters().equals(parameters);
    }
}
