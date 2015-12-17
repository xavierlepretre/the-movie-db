package com.github.xavierlepretre.tmdb.sql.movie;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.movie.Collection;
import com.github.xavierlepretre.tmdb.model.movie.CollectionContract;
import com.github.xavierlepretre.tmdb.model.movie.CollectionId;

public class CollectionCursor extends CursorWrapper
{
    private final int backdropPathCol;
    private final int idCol;
    private final int nameCol;
    private final int posterPathCol;

    public CollectionCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        backdropPathCol = getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH);
        idCol = getColumnIndex(CollectionContract._ID);
        nameCol = getColumnIndex(CollectionContract.COLUMN_NAME);
        posterPathCol = getColumnIndex(CollectionContract.COLUMN_POSTER_PATH);
    }

    @NonNull public Collection getCollection()
    {
        return new Collection(
                (backdropPathCol < 0 || isNull(backdropPathCol)) ? null : new ImagePath(getString(backdropPathCol)),
                (idCol < 0 || isNull(idCol)) ? null : new CollectionId(getInt(idCol)),
                nameCol < 0 ? null : getString(nameCol),
                (posterPathCol < 0 || isNull(posterPathCol)) ? null : new ImagePath(getString(posterPathCol)));
    }
}
