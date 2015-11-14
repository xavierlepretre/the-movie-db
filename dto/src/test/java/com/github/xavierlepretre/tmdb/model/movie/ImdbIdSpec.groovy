package com.github.xavierlepretre.tmdb.model.movie

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ImdbIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(ImdbId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
