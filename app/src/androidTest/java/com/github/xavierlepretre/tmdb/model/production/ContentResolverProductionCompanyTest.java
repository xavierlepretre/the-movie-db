package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.TmdbContract;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ProductionCompanyEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverProductionCompanyTest
{
    private Cursor cursor;
    private List<ContentObserver> observers;

    @Before
    public void setUp() throws Exception
    {
        deleteFromTable();
        observers = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception
    {
        if (cursor != null)
        {
            try
            {
                cursor.close();
            }
            catch (Exception ignored)
            {
                // Nothing to do
            }
        }
        deleteFromTable();
        for (ContentObserver observer : observers)
        {
            InstrumentationRegistry.getContext().getContentResolver().unregisterContentObserver(observer);
        }
        observers.clear();
    }

    private void deleteFromTable()
    {
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(ProductionCompanyEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                ProductionCompanyEntity.CONTENT_URI))
                .isEqualTo(ProductionCompanyEntity.CONTENT_DIR_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                ProductionCompanyEntity.buildUri(new ProductionCompanyId(5))))
                .isEqualTo(ProductionCompanyEntity.CONTENT_ITEM_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCompanyEntity.buildUri(new ProductionCompanyId(5)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCompanyCursor productionCompanyCursor = new ProductionCompanyCursor(cursor);
        assertThat(productionCompanyCursor.moveToFirst()).isTrue();
        ProductionCompany productionCompany = productionCompanyCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(5));
        assertThat(productionCompany.getName()).isEqualTo("Columbia Pictures");
        assertThat(productionCompanyCursor.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnProductionCompanyById_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCompanyEntity.buildUri(new ProductionCompanyId(5)),
                values);
    }

    @Test
    public void insert_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCompanyEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void insert_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCompanyEntity.CONTENT_URI);

        values.put(ProductionCompanyContract._ID, 6);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCompanyEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                ProductionCompanyEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCompanyCursor productionCompanyCursor = new ProductionCompanyCursor(cursor);
        assertThat(productionCompanyCursor.moveToFirst()).isTrue();
        ProductionCompany productionCompany = productionCompanyCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(5));
        assertThat(productionCompany.getName()).isEqualTo("Columbia Pictures");
        assertThat(productionCompanyCursor.moveToNext()).isTrue();
        productionCompany = productionCompanyCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(6));
        assertThat(productionCompany.getName()).isEqualTo("Danjaq");
        assertThat(productionCompanyCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCompanyEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                ProductionCompanyEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 5);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        ContentValues value3 = new ContentValues();
        value3.put(ProductionCompanyContract._ID, 6);
        value3.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCompanyEntity.CONTENT_URI,
                values))
                .isEqualTo(3);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                ProductionCompanyEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCompanyCursor productionCompanyCursor = new ProductionCompanyCursor(cursor);
        assertThat(productionCompanyCursor.moveToFirst()).isTrue();
        ProductionCompany productionCompany = productionCompanyCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(5));
        assertThat(productionCompany.getName()).isEqualTo("Fox");
        assertThat(productionCompanyCursor.moveToNext()).isTrue();
        productionCompany = productionCompanyCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(6));
        assertThat(productionCompany.getName()).isEqualTo("Danjaq");
        assertThat(productionCompanyCursor.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnProductionCompanyById_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                TmdbContract.ProductionCompanyEntity.buildUri(new ProductionCompanyId(5)),
                new ContentValues[]{value1, value2});
    }

    @Test
    public void insertBulk_getsNotified() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2};

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCompanyEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(ProductionCompanyEntity.CONTENT_URI);
    }

    @Test
    public void insertBulk_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2};
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCompanyEntity.CONTENT_URI);

        value1.put(ProductionCompanyContract._ID, 7);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        value2.put(ProductionCompanyContract._ID, 8);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Canal+");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCompanyEntity.buildUri(new ProductionCompanyId(5)).toString());

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void delete_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCompanyEntity.buildUri(new ProductionCompanyId(5)).toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCompanyEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void delete_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(1);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().delete(
                inserted, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCompanyEntity.CONTENT_URI);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().delete(
                inserted, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCompanyEntity.buildUri(new ProductionCompanyId(5)).toString());

        values.put(ProductionCompanyContract.COLUMN_NAME, "Fox");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCompanyCursor productionCompanyCursor = new ProductionCompanyCursor(cursor);
        assertThat(productionCompanyCursor.moveToFirst()).isTrue();
        ProductionCompany productionCompany = productionCompanyCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(5));
        assertThat(productionCompany.getName()).isEqualTo("Fox");
        assertThat(productionCompanyCursor.moveToNext()).isFalse();
    }

    @Test
    public void update_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCompanyEntity.CONTENT_URI,
                values);

        values.put(ProductionCompanyContract.COLUMN_NAME, "Fox");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCompanyEntity.CONTENT_URI,
                true,
                observer);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void update_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCompanyEntity.CONTENT_URI, null, null, null, null);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        values.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCompanyEntity.CONTENT_URI);

        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    private static class CountDownObserver extends ContentObserver
    {
        @NonNull private final CountDownLatch latch;
        public Uri latestChanged;

        public CountDownObserver(
                @Nullable Handler handler,
                @NonNull CountDownLatch latch)
        {
            super(handler);
            this.latch = latch;
        }

        @Override public void onChange(boolean selfChange, Uri uri)
        {
            super.onChange(selfChange, uri);
            latestChanged = uri;
            latch.countDown();
        }
    }
}
