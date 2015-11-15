package com.github.xavierlepretre.tmdb.model.movie

import com.github.xavierlepretre.tmdb.model.AppendableRequestFactory
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class MovieRequestParametersSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(MovieRequestParameters)
                .allFieldsShouldBeUsed()
                .withPrefabValues(
                Locale.class,
                new Locale("en"),
                new Locale("fr"))
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
                .verify()

        then:
        noExceptionThrown()
    }
}
