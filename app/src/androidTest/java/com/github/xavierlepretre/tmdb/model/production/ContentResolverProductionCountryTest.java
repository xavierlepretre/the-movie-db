package com.github.xavierlepretre.tmdb.model.production;

import com.github.xavierlepretre.tmdb.model.TmdbContract;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ProductionCountryEntity;
import com.neovisionaries.i18n.CountryCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverProductionCountryTest
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
                .delete(ProductionCountryEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                ProductionCountryEntity.CONTENT_URI))
                .isEqualTo(ProductionCountryEntity.CONTENT_DIR_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                ProductionCountryEntity.buildUri(CountryCode.AC)))
                .isEqualTo(ProductionCountryEntity.CONTENT_ITEM_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCountryEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCountryEntity.buildUri(CountryCode.GB).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCountryCursor productionCountryCursor = new ProductionCountryCursor(cursor);
        assertThat(productionCountryCursor.moveToFirst()).isTrue();
        ProductionCountry productionCountry = productionCountryCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(productionCountry.getName()).isEqualTo("United Kingdom");
        assertThat(productionCountryCursor.moveToNext()).isFalse();
    }

    @Test
    public void insert_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, 5);
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCountryEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCountryEntity.CONTENT_URI,
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
                TmdbContract.ProductionCountryEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, 5);
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCountryEntity.CONTENT_URI);

        values.put(ProductionCountryContract._ID, 6);
        values.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, "GB");
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCountryEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                ProductionCountryEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCountryCursor productionCountryCursor = new ProductionCountryCursor(cursor);
        assertThat(productionCountryCursor.moveToFirst()).isTrue();
        ProductionCountry productionCountry = productionCountryCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(productionCountry.getName()).isEqualTo("United Kingdom");
        assertThat(productionCountryCursor.moveToNext()).isTrue();
        productionCountry = productionCountryCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(productionCountry.getName()).isEqualTo("United States of America");
        assertThat(productionCountryCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCountryEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                ProductionCountryEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_skips1() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, "GB");
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};
        ContentValues value3 = new ContentValues();
        value3.put(ProductionCountryContract._ID, "GB");
        value3.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCountryEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                ProductionCountryEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCountryCursor productionCountryCursor = new ProductionCountryCursor(cursor);
        assertThat(productionCountryCursor.moveToFirst()).isTrue();
        ProductionCountry productionCountry = productionCountryCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(productionCountry.getName()).isEqualTo("United Kingdom");
        assertThat(productionCountryCursor.moveToNext()).isTrue();
        productionCountry = productionCountryCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(productionCountry.getName()).isEqualTo("United States of America");
        assertThat(productionCountryCursor.moveToNext()).isFalse();
    }

    @Test
    public void insertBulk_getsNotified() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, "GB");
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCountryEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ProductionCountryEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(ProductionCountryEntity.CONTENT_URI);
    }

    @Test
    public void insertBulk_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCountryEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, "GB");
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCountryEntity.CONTENT_URI);

        value1.put(ProductionCountryContract._ID, "FR");
        value1.put(ProductionCountryContract.COLUMN_NAME, "France");
        value2.put(ProductionCountryContract._ID, "DE");
        value2.put(ProductionCountryContract.COLUMN_NAME, "Germany");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCountryEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCountryEntity.buildUri(CountryCode.GB).toString());

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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCountryEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCountryEntity.buildUri(CountryCode.GB).toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCountryEntity.CONTENT_URI,
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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCountryEntity.CONTENT_URI, null, null, null, null);
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
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCountryEntity.CONTENT_URI);

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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCountryEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ProductionCountryEntity.buildUri(CountryCode.GB).toString());

        values.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        ProductionCountryCursor productionCountryCursor = new ProductionCountryCursor(cursor);
        assertThat(productionCountryCursor.moveToFirst()).isTrue();
        ProductionCountry productionCountry = productionCountryCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(productionCountry.getName()).isEqualTo("Royaume-Uni");
        assertThat(productionCountryCursor.moveToNext()).isFalse();
    }

    @Test
    public void update_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ProductionCountryEntity.CONTENT_URI,
                values);

        values.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ProductionCountryEntity.CONTENT_URI,
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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ProductionCountryEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ProductionCountryEntity.CONTENT_URI, null, null, null, null);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        values.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ProductionCountryEntity.CONTENT_URI);

        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
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
