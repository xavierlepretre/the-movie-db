package com.github.xavierlepretre.tmdb.model.tag

import com.github.xavierlepretre.tmdb.model.people.PersonId
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class KeywordIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(KeywordId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
