package com.github.xavierlepretre.tmdb.model.notify;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

public class NotificationList
{
    @NonNull private final Collection<Uri> toNotify;

    public NotificationList(@NonNull Collection<Uri> toNotify)
    {
        this.toNotify = Collections.unmodifiableCollection(toNotify);
    }

    @NonNull public Collection<Uri> getToNotify()
    {
        return toNotify;
    }
}
