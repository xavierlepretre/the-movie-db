package com.github.xavierlepretre.tmdb.model.rate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReviewsWithIdDTOTest
{
    private ObjectMapper mapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void canDeserialise() throws Exception
    {
        ReviewsWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("reviews_with_id_dto_1.json"), ReviewsWithIdDTO.class);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
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
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("reviews_with_id_dto_1_missing_id.json"), ReviewsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("page");
        mapper.readValue(getClass().getResourceAsStream("reviews_with_id_dto_1_missing_page.json"), ReviewsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("results");
        mapper.readValue(getClass().getResourceAsStream("reviews_with_id_dto_1_missing_results.json"), ReviewsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalPages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_pages");
        mapper.readValue(getClass().getResourceAsStream("reviews_with_id_dto_1_missing_total_pages.json"), ReviewsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_results");
        mapper.readValue(getClass().getResourceAsStream("reviews_with_id_dto_1_missing_total_results.json"), ReviewsWithIdDTO.class);
    }
}
