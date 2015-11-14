package com.github.xavierlepretre.tmdb.model.rate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReviewDTOTest
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
        ReviewDTO dto = mapper.readValue(getClass().getResourceAsStream("review_dto_1.json"), ReviewDTO.class);
        assertThat(dto.getAuthor()).isEqualTo("cutprintchris");
        assertThat(dto.getContent()).startsWith("<a href=\"http://www.cutprintfilm.com/r");
        assertThat(dto.getId()).isEqualTo(new ReviewId("563e06159251413b1300c821"));
        assertThat(dto.getUrl()).isEqualTo("http://j.mp/1MPodnZ");
    }

    @Test
    public void cannotDeserialiseMissingAuthor() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("author");
        mapper.readValue(getClass().getResourceAsStream("review_dto_1_missing_author.json"), ReviewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingContent() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("content");
        mapper.readValue(getClass().getResourceAsStream("review_dto_1_missing_content.json"), ReviewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("review_dto_1_missing_id.json"), ReviewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingUrl() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("url");
        mapper.readValue(getClass().getResourceAsStream("review_dto_1_missing_url.json"), ReviewDTO.class);
    }
}
