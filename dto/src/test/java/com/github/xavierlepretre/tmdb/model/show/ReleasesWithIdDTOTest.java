package com.github.xavierlepretre.tmdb.model.show;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReleasesWithIdDTOTest
{
    private ObjectMapper mapper;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void canDeserialise() throws Exception
    {
        ReleasesWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("releases_with_id_dto_1.json"), ReleasesWithIdDTO.class);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(dto.getCountries().size()).isEqualTo(59);
        assertThat(dto.getCountries().get(0).getCertification()).isEmpty();
        assertThat(dto.getCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getCountries().get(0).isPrimary()).isFalse();
        assertThat(dto.getCountries().get(0).getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getCountries().get(1).getCertification()).isEqualTo("PG-13");
        assertThat(dto.getCountries().get(1).getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.getCountries().get(1).isPrimary()).isTrue();
        assertThat(dto.getCountries().get(1).getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
    }

    @Test
    public void cannotDeserialiseMissingCountries() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("countries");
        mapper.readValue(getClass().getResourceAsStream("releases_with_id_dto_1_missing_countries.json"), ReleasesWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("releases_with_id_dto_1_missing_id.json"), ReleasesWithIdDTO.class);
    }
}
