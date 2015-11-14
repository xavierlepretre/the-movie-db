package com.github.xavierlepretre.tmdb.model.tag

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ListIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(ListId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
