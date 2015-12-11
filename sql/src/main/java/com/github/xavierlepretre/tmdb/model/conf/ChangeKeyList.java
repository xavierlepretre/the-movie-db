package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class ChangeKeyList extends ArrayList<ChangeKey>
{
    private static final String SEPARATOR = ",";

    public ChangeKeyList(@NonNull String joinedKeys)
    {
        super();
        String[] splitKeys = joinedKeys.split(SEPARATOR);
        for (String splitKey : splitKeys)
        {
            add(new ChangeKey(splitKey));
        }
    }

    public ChangeKeyList(int capacity)
    {
        super(capacity);
    }

    public ChangeKeyList()
    {
    }

    public ChangeKeyList(@NonNull Collection<? extends ChangeKey> collection)
    {
        super(collection);
    }

    @NonNull public String join()
    {
        StringBuilder builder = new StringBuilder();
        String separator = "";
        for (ChangeKey changeKey : this)
        {
            builder.append(separator).append(changeKey.getKey());
            separator = SEPARATOR;
        }
        return builder.toString();
    }
}
