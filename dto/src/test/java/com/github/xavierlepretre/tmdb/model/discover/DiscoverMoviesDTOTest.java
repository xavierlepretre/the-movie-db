package com.github.xavierlepretre.tmdb.model.discover;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class DiscoverMoviesDTOTest
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
        DiscoverMoviesDTO dto = mapper.readValue(getClass().getResourceAsStream("discover_movies_dto_1.json"), DiscoverMoviesDTO.class);
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getTotalPages()).isEqualTo(12525);
        assertThat(dto.getTotalResults()).isEqualTo(250492);
    }

    @Test
    public void cannotDeserialiseMissingPage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("page");
        mapper.readValue(getClass().getResourceAsStream("discover_movies_dto_1_missing_page.json"), DiscoverMoviesDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalPages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_pages");
        mapper.readValue(getClass().getResourceAsStream("discover_movies_dto_1_missing_total_pages.json"), DiscoverMoviesDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_results");
        mapper.readValue(getClass().getResourceAsStream("discover_movies_dto_1_missing_total_results.json"), DiscoverMoviesDTO.class);
    }
}
