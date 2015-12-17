package com.github.xavierlepretre.tmdb.sql;

import android.support.annotation.NonNull;

public interface EntitySQLHelperDelegate
{
    @NonNull String getCreateQuery();

    @NonNull String getUpgradeQuery(int oldVersion, int newVersion);
}
