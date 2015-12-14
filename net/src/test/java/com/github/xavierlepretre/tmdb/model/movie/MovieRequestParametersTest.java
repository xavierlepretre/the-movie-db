package com.github.xavierlepretre.tmdb.model.movie;

import com.github.xavierlepretre.tmdb.model.AppendableRequestFactory;
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MovieRequestParametersTest
{
    @Test
    public void equalsHashcode_isOk() throws Exception
    {
        EqualsVerifier.forClass(MovieRequestParameters.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(
                        LanguageCode.class,
                        LanguageCode.en,
                        LanguageCode.fr)
                .withPrefabValues(
                        AppendableRequestSet.class,
                        new AppendableRequestSet(Arrays.asList(
                                new AppendableRequestFactory().create("a"),
                                new AppendableRequestFactory().create("b")
                        )),
                        new AppendableRequestSet(Arrays.asList(
                                new AppendableRequestFactory().create("b"),
                                new AppendableRequestFactory().create("c")
                        )))
                .verify();
    }
}
