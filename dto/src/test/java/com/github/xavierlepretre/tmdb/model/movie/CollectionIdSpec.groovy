package com.github.xavierlepretre.tmdb.model.movie

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class CollectionIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(CollectionId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
