package com.github.xavierlepretre.tmdb.sync;

import android.os.Bundle;

import org.junit.Test;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class TmdbSyncConstantsTest
{
    @Test
    public void canPutAllParametersTogether() throws Exception
    {
        Bundle extras = new Bundle();
        TmdbSyncConstants.putLanguage(extras, new Locale("vn"));

        assertThat(TmdbSyncConstants.getLanguage(extras)).isEqualTo(new Locale("vn"));
    }
}