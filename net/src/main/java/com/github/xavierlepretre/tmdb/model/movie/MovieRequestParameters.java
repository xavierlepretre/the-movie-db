package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.AppendableRequest;
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class MovieRequestParameters
{
    @Nullable private final Locale language;
    @NonNull private final AppendableRequestSet appendToResponse;

    public MovieRequestParameters()
    {
        this(null, new AppendableRequestSet());
    }

    public MovieRequestParameters(
            @Nullable Locale language,
            @NonNull AppendableRequestSet appendToResponse)
    {
        this.language = language;
        this.appendToResponse = appendToResponse;
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
        return (language == null ? 0 : language.hashCode())
                ^ appendToResponse.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        if (!(obj instanceof MovieRequestParameters))
        {
            return false;
        }
        MovieRequestParameters other = (MovieRequestParameters) obj;
        return (other.getLanguage() == null
                ? language == null
                : other.getLanguage().equals(language))
                && other.getAppendToResponse().equals(appendToResponse);
    }

    public static class Builder
    {
        @Nullable private Locale language;
        @NonNull private AppendableRequestSet appendToResponse;

        public Builder()
        {
            this.appendToResponse = new AppendableRequestSet();
        }

        @Nullable protected Locale getLanguage()
        {
            return language;
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

        @NonNull protected AppendableRequestSet getAppendToResponse()
        {
            return appendToResponse;
        }

        @NonNull public Builder appendToResponse(@NonNull Collection<? extends AppendableRequest> appendToResponse)
        {
            this.appendToResponse.addAll(appendToResponse);
            return this;
        }

        @NonNull public Builder appendToResponse(@NonNull AppendableRequest[] appendToResponse)
        {
            Collections.addAll(this.appendToResponse, appendToResponse);
            return this;
        }

        @NonNull public Builder appendToResponse(@NonNull AppendableRequest appendToResponse)
        {
            this.appendToResponse.add(appendToResponse);
            return this;
        }

        @NonNull public MovieRequestParameters build()
        {
            return new MovieRequestParameters(
                    language,
                    appendToResponse);
        }
    }
}
