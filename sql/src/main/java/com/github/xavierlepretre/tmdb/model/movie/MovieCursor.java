package com.github.xavierlepretre.tmdb.model.movie;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.neovisionaries.i18n.LanguageCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MovieCursor extends CursorWrapper
{
    @NonNull private final SimpleDateFormat formatter;
    private final int adultCol;
    private final int backdropPathCol;
    private final int belongsToCollectionIdCol;
    private final int budgetCol;
    private final int homepageCol;
    private final int idCol;
    private final int imdbIdCol;
    private final int originalLanguageCol;
    private final int originalTitleCol;
    private final int overviewCol;
    private final int popularityCol;
    private final int posterPathCol;
    private final int releaseDateCol;
    private final int revenueCol;
    private final int runtimeCol;
    private final int statusCol;
    private final int taglineCol;
    private final int titleCol;
    private final int videoCol;
    private final int voteAverageCol;
    private final int voteCountCol;

    @SuppressLint("SimpleDateFormat")
    public MovieCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        formatter = new SimpleDateFormat(MovieContract.RELEASE_DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(MovieContract.RELEASE_DATE_TIME_ZONE));
        adultCol = getColumnIndex(MovieContract.COLUMN_ADULT);
        backdropPathCol = getColumnIndex(MovieContract.COLUMN_BACKDROP_PATH);
        belongsToCollectionIdCol = getColumnIndex(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID);
        budgetCol = getColumnIndex(MovieContract.COLUMN_BUDGET);
        homepageCol = getColumnIndex(MovieContract.COLUMN_HOMEPAGE);
        idCol = getColumnIndex(MovieContract._ID);
        imdbIdCol = getColumnIndex(MovieContract.COLUMN_IMDB_ID);
        originalLanguageCol = getColumnIndex(MovieContract.COLUMN_ORIGINAL_LANGUAGE);
        originalTitleCol = getColumnIndex(MovieContract.COLUMN_ORIGINAL_TITLE);
        overviewCol = getColumnIndex(MovieContract.COLUMN_OVERVIEW);
        popularityCol = getColumnIndex(MovieContract.COLUMN_POPULARITY);
        posterPathCol = getColumnIndex(MovieContract.COLUMN_POSTER_PATH);
        releaseDateCol = getColumnIndex(MovieContract.COLUMN_RELEASE_DATE);
        revenueCol = getColumnIndex(MovieContract.COLUMN_REVENUE);
        runtimeCol = getColumnIndex(MovieContract.COLUMN_RUNTIME);
        statusCol = getColumnIndex(MovieContract.COLUMN_STATUS);
        taglineCol = getColumnIndex(MovieContract.COLUMN_TAGLINE);
        titleCol = getColumnIndex(MovieContract.COLUMN_TITLE);
        videoCol = getColumnIndex(MovieContract.COLUMN_VIDEO);
        voteAverageCol = getColumnIndex(MovieContract.COLUMN_VOTE_AVERAGE);
        voteCountCol = getColumnIndex(MovieContract.COLUMN_VOTE_COUNT);
    }

    @NonNull public Movie getMovie()
    {
        String releaseDateText = releaseDateCol < 0 ? null : getString(releaseDateCol);
        Date releaseDate = null;
        try
        {
            if (releaseDateText != null)
            {
                releaseDate = formatter.parse(releaseDateText);
            }
        }
        catch (ParseException ignored)
        {
        }
        return new Movie(
                adultCol < 0 ? null : (getInt(adultCol) > 0),
                backdropPathCol < 0 ? null : new ImagePath(getString(backdropPathCol)),
                belongsToCollectionIdCol < 0 ? null : new CollectionId(getLong(belongsToCollectionIdCol)),
                budgetCol < 0 ? null : getLong(budgetCol),
                homepageCol < 0 ? null : getString(homepageCol),
                idCol < 0 ? null : new MovieId(getInt(idCol)),
                imdbIdCol < 0 ? null : new ImdbId(getString(imdbIdCol)),
                originalLanguageCol < 0 ? null : LanguageCode.getByCode(getString(originalLanguageCol)),
                originalTitleCol < 0 ? null : getString(originalTitleCol),
                overviewCol < 0 ? null : getString(overviewCol),
                popularityCol < 0 ? null : getFloat(popularityCol),
                posterPathCol < 0 ? null : new ImagePath(getString(posterPathCol)),
                releaseDate,
                revenueCol < 0 ? null : getLong(revenueCol),
                runtimeCol < 0 ? null : getInt(runtimeCol),
                statusCol < 0 ? null : getString(statusCol),
                taglineCol < 0 ? null : getString(taglineCol),
                titleCol < 0 ? null : getString(titleCol),
                videoCol < 0 ? null : getInt(videoCol) > 0,
                voteAverageCol < 0 ? null : getFloat(voteAverageCol),
                voteCountCol < 0 ? null : getInt(voteCountCol));
    }
}
