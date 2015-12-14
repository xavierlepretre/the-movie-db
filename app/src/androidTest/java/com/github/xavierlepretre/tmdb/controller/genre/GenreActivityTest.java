package com.github.xavierlepretre.tmdb.controller.genre;

import com.github.xavierlepretre.tmdb.model.TmdbContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.content.ContentValues;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class GenreActivityTest
{
    @Rule
    public ActivityTestRule<GenreActivity> activityTestRule = new ActivityTestRule<>(GenreActivity.class);

    @Before
    public void setUp() throws Exception
    {
        deleteFromTable();
    }

    @After
    public void tearDown() throws Exception
    {
        deleteFromTable();
    }

    private void deleteFromTable()
    {
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(TmdbContract.GenreEntity.CONTENT_URI, null, null);
    }

    @Test
    public void newGenre_displaysUpdates() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.GenreEntity.CONTENT_URI,
                values);

        onView(withText("Adventure")).check(matches(isDisplayed()));
        onView(withText("Comic")).check(doesNotExist());

        values.put(GenreContract._ID, 4);
        values.put(GenreContract.COLUMN_NAME, "Comic");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.GenreEntity.CONTENT_URI,
                values);

        onView(withText("Comic")).check(matches(isDisplayed()));
    }
}