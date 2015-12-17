package com.github.xavierlepretre.tmdb.sql.notify;

import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.Collection;

public class NotificationListWithCount extends NotificationList
{
    @IntRange(from = -1)
    private final int count;

    public NotificationListWithCount(
            @NonNull Collection<Uri> toNotify,
            @IntRange(from = -1) int count)
    {
        super(toNotify);
        this.count = count;
    }

    @IntRange(from = -1) public int getCount()
    {
        return count;
    }
}
