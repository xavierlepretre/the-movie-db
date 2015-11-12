package com.github.xavierlepretre.tmdb.net;

import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;

import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit.Call;
import retrofit.Response;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class TmdbRetrofitRequestTest
{
    @Test @FlakyTest(tolerance = 3)
    public void canGetConfiguration() throws Exception
    {
        Response<ConfigurationDTO> response = new TmdbRetrofitFactory()
                .create()
                .getConfiguration(BuildConfig.TMDB_API_KEY)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.body().getImagesConf().getBaseUrl()).isEqualTo("http://image.tmdb.org/t/p/");
    }
}
