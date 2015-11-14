package com.github.xavierlepretre.tmdb.model.movie;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class TitleDTOTest
{
    private ObjectMapper mapper;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void canDeserialise1() throws Exception
    {
        TitleDTO dto = mapper.readValue(getClass().getResourceAsStream("title_dto_1.json"), TitleDTO.class);
        assertThat(dto.getIso3166Dash1()).isEqualTo(CountryCode.TW);
        assertThat(dto.getTitle()).isEqualTo("007惡魔四伏");
    }

    @Test
    public void cannotDeserialiseMissingIso3166Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_3166_1");
        mapper.readValue(getClass().getResourceAsStream("title_dto_1_missing_iso_3166_1.json"), TitleDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("title");
        mapper.readValue(getClass().getResourceAsStream("title_dto_1_missing_title.json"), TitleDTO.class);
    }
}