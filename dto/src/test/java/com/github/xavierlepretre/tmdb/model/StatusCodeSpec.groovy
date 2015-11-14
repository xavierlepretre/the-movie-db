package com.github.xavierlepretre.tmdb.model

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class StatusCodeSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(StatusCode)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
