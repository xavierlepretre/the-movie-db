package com.github.xavierlepretre.tmdb.model

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class AppendableRequestSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(AppendableRequest)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
