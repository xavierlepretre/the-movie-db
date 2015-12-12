package com.github.xavierlepretre.tmdb.model.people;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class CrewDTOTest
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
        CrewDTO dto = mapper.readValue(getClass().getResourceAsStream("crew_dto_1.json"), CrewDTO.class);
        assertThat(dto.getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getDepartment()).isEqualTo("Writing");
        assertThat(dto.getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getJob()).isEqualTo("Characters");
        assertThat(dto.getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getProfilePath()).isEqualTo(new ImagePath("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg"));
    }

    @Test
    public void cannotDeserialiseMissingCreditId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("credit_id");
        mapper.readValue(getClass().getResourceAsStream("crew_dto_1_missing_credit_id.json"), CrewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingDepartment() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("department");
        mapper.readValue(getClass().getResourceAsStream("crew_dto_1_missing_department.json"), CrewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("crew_dto_1_missing_id.json"), CrewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingJob() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("job");
        mapper.readValue(getClass().getResourceAsStream("crew_dto_1_missing_job.json"), CrewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("crew_dto_1_missing_name.json"), CrewDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProfilePath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("profile_path");
        mapper.readValue(getClass().getResourceAsStream("crew_dto_1_missing_profile_path.json"), CrewDTO.class);
    }
}
