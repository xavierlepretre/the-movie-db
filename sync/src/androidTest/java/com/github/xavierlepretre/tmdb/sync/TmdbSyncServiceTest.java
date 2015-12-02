package com.github.xavierlepretre.tmdb.sync;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.fest.assertions.api.Assertions.assertThat;

public class TmdbSyncServiceTest
{
    @Test
    public void binder_isChildClassOfTmdbSyncAdapter() throws Exception
    {
        TmdbSyncService service = new TmdbSyncService();
        service.onCreate();
        Class adapterImplClass = getClass().getClassLoader().loadClass(AbstractThreadedSyncAdapter.class.getName() + "$ISyncAdapterImpl");
        IBinder binder = service.onBind(new Intent());
        Field field = adapterImplClass.getDeclaredField("this$0");
        field.setAccessible(true);
        Object syncAdapter = field.get(binder);

        assertThat(syncAdapter).isInstanceOf(TmdbSyncAdapter.class);
    }

    @Test
    public void syncAdapter_isStatic() throws Exception
    {
        TmdbSyncService service1 = new TmdbSyncService();
        service1.onCreate();
        TmdbSyncService service2 = new TmdbSyncService();
        service2.onCreate();

        assertThat(service1.onBind(new Intent())).isSameAs(service2.onBind(new Intent()));
    }
}