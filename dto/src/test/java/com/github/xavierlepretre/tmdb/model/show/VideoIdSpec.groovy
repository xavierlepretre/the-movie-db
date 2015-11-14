package com.github.xavierlepretre.tmdb.model.show

import com.github.xavierlepretre.tmdb.model.people.PersonId
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class VideoIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(VideoId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
