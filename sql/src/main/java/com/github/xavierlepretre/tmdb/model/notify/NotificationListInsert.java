package com.github.xavierlepretre.tmdb.model.notify;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

public class NotificationListInsert extends NotificationList
{
    @Nullable private final Uri inserted;

    public NotificationListInsert(
            @NonNull Collection<Uri> toNotify,
            @Nullable Uri inserted)
    {
        super(toNotify);
        this.inserted = inserted;
    }

    @Nullable public Uri getInserted()
    {
        return inserted;
    }
}
