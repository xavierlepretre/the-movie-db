package com.github.xavierlepretre.tmdb.sql;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CollectionHelper
{
    @NonNull public <T> List<List<T>> getAllSubCollections(@NonNull T[] available)
    {
        if (available.length == 0)
        {
            return new ArrayList<>();
        }
        return getAllSubCollections(available, available.length - 1);
    }

    @NonNull private <T> List<List<T>> getAllSubCollections(T[] available, int startIndex)
    {
        if (startIndex == 0)
        {
            return Arrays.asList(
                    Collections.<T>emptyList(),
                    Collections.singletonList(available[startIndex]));
        }
        List<List<T>> fromRight = getAllSubCollections(available, startIndex - 1);
        List<List<T>> combined = new ArrayList<>();

        for (List<T> collection : fromRight)
        {
            combined.add(collection);
            List<T> withMore = new ArrayList<>(collection);
            withMore.add(available[startIndex]);
            combined.add(withMore);
        }

        return combined;
    }
}
