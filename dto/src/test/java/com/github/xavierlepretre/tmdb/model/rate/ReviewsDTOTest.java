package com.github.xavierlepretre.tmdb.model.rate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReviewsDTOTest
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
        ReviewsDTO dto = mapper.readValue(getClass().getResourceAsStream("reviews_dto_1.json"), ReviewsDTO.class);
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isEqualTo(2);
        assertThat(dto.getResults().get(0).getAuthor()).isEqualTo("cutprintchris");
        assertThat(dto.getResults().get(0).getContent()).startsWith("<a href=\"http://www.cutprintfilm.com/r");
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new ReviewId("563e06159251413b1300c821"));
        assertThat(dto.getResults().get(0).getUrl()).isEqualTo("http://j.mp/1MPodnZ");
        assertThat(dto.getTotalPages()).isEqualTo(1);
        assertThat(dto.getTotalResults()).isEqualTo(2);
    }

    @Test
    public void cannotDeserialiseMissingPage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("page");
        mapper.readValue(getClass().getResourceAsStream("reviews_dto_1_missing_page.json"), ReviewsDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("results");
        mapper.readValue(getClass().getResourceAsStream("reviews_dto_1_missing_results.json"), ReviewsDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalPages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_pages");
        mapper.readValue(getClass().getResourceAsStream("reviews_dto_1_missing_total_pages.json"), ReviewsDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_results");
        mapper.readValue(getClass().getResourceAsStream("reviews_dto_1_missing_total_results.json"), ReviewsDTO.class);
    }
}
