package com.github.xavierlepretre.tmdb.model.image;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class ImageSizeList extends ArrayList<ImageSize>
{
    private static final String SEPARATOR = ",";

    public ImageSizeList(@NonNull String joinedKeys)
    {
        super();
        String[] splitKeys = joinedKeys.split(SEPARATOR);
        for (String splitKey : splitKeys)
        {
            add(new ImageSize(splitKey));
        }
    }

    public ImageSizeList(int capacity)
    {
        super(capacity);
    }

    public ImageSizeList()
    {
    }

    public ImageSizeList(@NonNull Collection<? extends ImageSize> collection)
    {
        super(collection);
    }

    @NonNull public String join()
    {
        StringBuilder builder = new StringBuilder();
        String separator = "";
        for (ImageSize ImageSize : this)
        {
            builder.append(separator).append(ImageSize.getSize());
            separator = SEPARATOR;
        }
        return builder.toString();
    }
}
