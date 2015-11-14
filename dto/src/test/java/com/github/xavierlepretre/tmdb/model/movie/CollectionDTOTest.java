package com.github.xavierlepretre.tmdb.model.movie;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class CollectionDTOTest
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
        CollectionDTO dto = mapper.readValue(getClass().getResourceAsStream("collection_dto_1.json"), CollectionDTO.class);
        assertThat(dto.getBackdropPath()).isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(dto.getId()).isEqualTo(new CollectionId(645));
        assertThat(dto.getName()).isEqualTo("James Bond Collection");
        assertThat(dto.getPosterPath()).isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void cannotDeserialiseMissingBackdropPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("backdrop_path");
        mapper.readValue(getClass().getResourceAsStream("collection_dto_1_missing_backdrop_path.json"), CollectionDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("collection_dto_1_missing_id.json"), CollectionDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("collection_dto_1_missing_name.json"), CollectionDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPosterPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("poster_path");
        mapper.readValue(getClass().getResourceAsStream("collection_dto_1_missing_poster_path.json"), CollectionDTO.class);
    }
}
