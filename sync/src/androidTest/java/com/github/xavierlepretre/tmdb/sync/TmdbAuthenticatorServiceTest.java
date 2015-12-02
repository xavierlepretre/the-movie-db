package com.github.xavierlepretre.tmdb.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.content.Intent;
import android.os.IBinder;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.fest.assertions.api.Assertions.assertThat;

public class TmdbAuthenticatorServiceTest
{
    @Test
    public void binder_isChildClassOfTmdbAuthenticator() throws Exception
    {
        TmdbAuthenticatorService service = new TmdbAuthenticatorService();
        service.onCreate();
        Class transportClass = getClass().getClassLoader().loadClass(AbstractAccountAuthenticator.class.getName() + "$Transport");
        IBinder binder = service.onBind(new Intent());
        Field field = transportClass.getDeclaredField("this$0");
        field.setAccessible(true);
        Object authenticator = field.get(binder);

        assertThat(authenticator).isInstanceOf(TmdbAuthenticator.class);
    }
}