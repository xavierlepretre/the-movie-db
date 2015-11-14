package com.github.xavierlepretre.tmdb.model.tag;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class KeywordDTOTest
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
        KeywordDTO dto = mapper.readValue(getClass().getResourceAsStream("keyword_dto_1.json"), KeywordDTO.class);
        assertThat(dto.getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getName()).isEqualTo("james bond");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("keyword_dto_1_missing_id.json"), KeywordDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("keyword_dto_1_missing_name.json"), KeywordDTO.class);
    }
}
