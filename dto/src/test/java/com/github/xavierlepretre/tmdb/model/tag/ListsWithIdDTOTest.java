package com.github.xavierlepretre.tmdb.model.tag;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class ListsWithIdDTOTest
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
        ListsWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("lists_with_id_dto_1.json"), ListsWithIdDTO.class);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getResults().size()).isEqualTo(20);
        assertThat(dto.getResults().get(0).getDescription()).isEqualTo("James Bond");
        assertThat(dto.getResults().get(0).getFavoriteCount()).isEqualTo(5);
        assertThat(dto.getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getResults().get(0).getItemCount()).isEqualTo(26);
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getResults().get(0).getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getResults().get(0).getPosterPath()).isEqualTo(new ImagePath("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg"));
        assertThat(dto.getTotalPages()).isEqualTo(2);
        assertThat(dto.getTotalResults()).isEqualTo(29);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("lists_with_id_dto_1_missing_id.json"), ListsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("page");
        mapper.readValue(getClass().getResourceAsStream("lists_with_id_dto_1_missing_page.json"), ListsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("results");
        mapper.readValue(getClass().getResourceAsStream("lists_with_id_dto_1_missing_results.json"), ListsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalPages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_pages");
        mapper.readValue(getClass().getResourceAsStream("lists_with_id_dto_1_missing_total_pages.json"), ListsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTotalResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("total_results");
        mapper.readValue(getClass().getResourceAsStream("lists_with_id_dto_1_missing_total_results.json"), ListsWithIdDTO.class);
    }
}
