package com.github.xavierlepretre.tmdb.model.movie;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class AlternativeTitlesWithIdDTOTest
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
        AlternativeTitlesWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("alternative_titles_with_id_dto_1.json"), AlternativeTitlesWithIdDTO.class);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getTitles().size()).isEqualTo(6);
        assertThat(dto.getTitles().get(1).getIso3166Dash1()).isEqualTo(CountryCode.ES);
        assertThat(dto.getTitles().get(1).getTitle()).isEqualTo("007 James Bond: Spectre");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("alternative_titles_with_id_dto_1_missing_id.json"), AlternativeTitlesWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTitles() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("titles");
        mapper.readValue(getClass().getResourceAsStream("alternative_titles_with_id_dto_1_missing_titles.json"), AlternativeTitlesWithIdDTO.class);
    }
}
