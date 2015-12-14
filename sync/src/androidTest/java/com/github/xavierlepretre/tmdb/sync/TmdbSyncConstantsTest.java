package com.github.xavierlepretre.tmdb.sync;

import android.os.Bundle;

import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;


import static org.fest.assertions.api.Assertions.assertThat;

public class TmdbSyncConstantsTest
{
    @Test
    public void canPutAllParametersTogether() throws Exception
    {
        Bundle extras = new Bundle();
        TmdbSyncConstants.putLanguage(extras, LanguageCode.vi);

        assertThat(TmdbSyncConstants.getLanguage(extras)).isEqualTo(LanguageCode.vi);
    }
}