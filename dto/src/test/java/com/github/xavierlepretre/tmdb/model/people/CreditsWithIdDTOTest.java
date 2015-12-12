package com.github.xavierlepretre.tmdb.model.people;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class CreditsWithIdDTOTest
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
        CreditsWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("credits_with_id_dto_1.json"), CreditsWithIdDTO.class);
        assertThat(dto.getCast().size()).isEqualTo(16);
        assertThat(dto.getCast().get(0).getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCast().get(0).getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCast().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getCast().get(0).getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getCast().get(0).getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getCast().get(0).getOrder()).isEqualTo(0);
        assertThat(dto.getCast().get(0).getProfilePath()).isEqualTo(new ImagePath("/cO5OUQAMM6a4Rndw5Hc81KgpF5p.jpg"));
        assertThat(dto.getCrew().size()).isEqualTo(15);
        assertThat(dto.getCrew().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getCrew().get(0).getDepartment()).isEqualTo("Writing");
        assertThat(dto.getCrew().get(0).getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getCrew().get(0).getJob()).isEqualTo("Characters");
        assertThat(dto.getCrew().get(0).getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getCrew().get(0).getProfilePath()).isEqualTo(new ImagePath("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg"));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
    }

    @Test
    public void cannotDeserialiseMissingCast() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("cast");
        mapper.readValue(getClass().getResourceAsStream("credits_with_id_dto_1_missing_cast.json"), CreditsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingCrew() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("crew");
        mapper.readValue(getClass().getResourceAsStream("credits_with_id_dto_1_missing_crew.json"), CreditsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("credits_with_id_dto_1_missing_id.json"), CreditsWithIdDTO.class);
    }
}
