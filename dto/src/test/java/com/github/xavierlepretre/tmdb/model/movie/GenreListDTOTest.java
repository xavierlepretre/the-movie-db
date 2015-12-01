package com.github.xavierlepretre.tmdb.model.movie;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class GenreListDTOTest
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
        GenreListDTO dto = mapper.readValue(getClass().getResourceAsStream("genre_list_dto_1.json"), GenreListDTO.class);
        assertThat(dto.getGenres().size()).isEqualTo(20);
        assertThat(dto.getGenres().get(1).getId()).isEqualTo(new GenreId(12));
        assertThat(dto.getGenres().get(1).getName()).isEqualTo("Adventure");
    }

    @Test
    public void cannotDeserialiseMissingGenres() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("genres");
        mapper.readValue(getClass().getResourceAsStream("genre_list_dto_1_missing_genres.json"), GenreListDTO.class);
    }
}
