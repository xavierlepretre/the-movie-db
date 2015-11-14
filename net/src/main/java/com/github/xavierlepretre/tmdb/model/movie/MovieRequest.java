package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.AppendableRequest;
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet;

import java.util.Locale;

public class MovieRequest
{
    @NonNull private final MovieId movieId;
    @Nullable private final Locale language;
    @NonNull private final AppendableRequestSet appendToResponse;

    public MovieRequest(@NonNull MovieId movieId)
    {
        this(movieId, null, new AppendableRequestSet());
    }

    public MovieRequest(
            @NonNull MovieId movieId,
            @Nullable Locale language,
            @NonNull AppendableRequestSet appendToResponse)
    {
        this.movieId = movieId;
        this.language = language;
        this.appendToResponse = appendToResponse;
    }

    @NonNull public MovieId getMovieId()
    {
        return movieId;
    }

    @Nullable public Locale getLanguage()
    {
        return language;
    }

    @NonNull public AppendableRequestSet getAppendToResponse()
    {
        return new AppendableRequestSet(appendToResponse);
    }

    @Override public final int hashCode()
    {
        return movieId.hashCode()
                ^ (language == null ? 0 : language.hashCode())
                ^ appendToResponse.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        if (!(obj instanceof MovieRequest))
        {
            return false;
        }
        MovieRequest other = (MovieRequest) obj;
        return other.getMovieId().equals(movieId)
                && (other.getLanguage() == null
                ? language == null
                : other.getLanguage().equals(language))
                && other.getAppendToResponse().equals(appendToResponse);
    }

    public static class Builder
    {
        @NonNull private final MovieId movieId;
        @Nullable private Locale language;
        @NonNull private AppendableRequestSet appendToResponse;

        public Builder(@NonNull MovieId movieId)
        {
            this.movieId = movieId;
            this.appendToResponse = new AppendableRequestSet();
        }

        @NonNull public Builder language(@Nullable Locale language)
        {
            this.language = language;
            return this;
        }

        @NonNull public Builder language(@NonNull String language)
        {
            this.language = new Locale(language);
            return this;
        }

        @NonNull public Builder appendToResponse(@NonNull AppendableRequestSet appendToResponse)
        {
            this.appendToResponse = appendToResponse;
            return this;
        }

        @NonNull public Builder add(@NonNull AppendableRequest appendToResponse)
        {
            this.appendToResponse.add(appendToResponse);
            return this;
        }

        @NonNull public MovieRequest build()
        {
            return new MovieRequest(
                    movieId,
                    language,
                    appendToResponse);
        }
    }
}
