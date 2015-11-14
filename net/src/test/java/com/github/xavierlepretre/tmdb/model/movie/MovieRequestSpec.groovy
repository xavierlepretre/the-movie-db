package com.github.xavierlepretre.tmdb.model.movie

import com.github.xavierlepretre.tmdb.model.AppendableRequest
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class MovieRequestSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(MovieRequest)
                .allFieldsShouldBeUsed()
                .withPrefabValues(
                AppendableRequestSet.class,
                new AppendableRequestSet(Arrays.asList(
                        new AppendableRequest("a"),
                        new AppendableRequest("b")
                )),
                new AppendableRequestSet(Arrays.asList(
                        new AppendableRequest("b"),
                        new AppendableRequest("c")
                )))
                .verify()

        then:
        noExceptionThrown()
    }
}
