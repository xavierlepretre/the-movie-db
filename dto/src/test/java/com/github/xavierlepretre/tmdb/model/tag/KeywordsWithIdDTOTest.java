package com.github.xavierlepretre.tmdb.model.tag;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class KeywordsWithIdDTOTest
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
        KeywordsWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("keyword_with_id_dto_1.json"), KeywordsWithIdDTO.class);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getKeywords().size()).isEqualTo(4);
        assertThat(dto.getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().get(0).getName()).isEqualTo("james bond");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("keyword_with_id_dto_1_missing_id.json"), KeywordsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingKeywords() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("keywords");
        mapper.readValue(getClass().getResourceAsStream("keyword_with_id_dto_1_missing_keywords.json"), KeywordsWithIdDTO.class);
    }
}
