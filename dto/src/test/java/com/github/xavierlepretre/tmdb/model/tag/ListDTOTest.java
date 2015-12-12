package com.github.xavierlepretre.tmdb.model.tag;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class ListDTOTest
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
        ListDTO dto = mapper.readValue(getClass().getResourceAsStream("list_dto_1.json"), ListDTO.class);
        assertThat(dto.getDescription()).isEqualTo("James Bond");
        assertThat(dto.getFavoriteCount()).isEqualTo(5);
        assertThat(dto.getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getItemCount()).isEqualTo(26);
        assertThat(dto.getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getPosterPath()).isEqualTo(new ImagePath("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg"));
    }

    @Test
    public void cannotDeserialiseMissingDescription() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("description");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_description.json"), ListDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingFavoriteCount() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("favorite_count");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_favorite_count.json"), ListDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_id.json"), ListDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingIso639Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_639_1");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_iso_639_1.json"), ListDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingItemCount() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("item_count");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_item_count.json"), ListDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_name.json"), ListDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPosterPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("poster_path");
        mapper.readValue(getClass().getResourceAsStream("list_dto_1_missing_poster_path.json"), ListDTO.class);
    }
}
