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
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieShortDTOTest
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
        MovieShortDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1.json"), MovieShortDTO.class);
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo("/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg");
        assertThat(dto.getGenreIds()).isEqualTo(Arrays.asList(new GenreId(28), new GenreId(12), new GenreId(80)));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getPosterPath()).isEqualTo("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg");
        assertThat(dto.getPopularity()).isEqualTo(57.231904f, Offset.offset(0.01f));
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isEqualTo(6.7f);
        assertThat(dto.getVoteCount()).isEqualTo(453);
    }

    @Test
    public void cannotDeserialiseMissingAdult() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("adult");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_adult.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBackdropPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("backdrop_path");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_backdrop_path.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingGenreIds() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("genre_ids");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_genre_ids.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_id.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOriginalLanguage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("original_language");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_original_language.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOriginalTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("original_title");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_original_title.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOverview() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("overview");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_overview.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPopularity() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("popularity");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_popularity.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPosterPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("poster_path");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_poster_path.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingReleaseDate() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("release_date");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_release_date.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("title");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_title.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVideo() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("video");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_video.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteAverage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_average");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_vote_average.json"), MovieShortDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteCount() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_count");
        mapper.readValue(getClass().getResourceAsStream("movie_short_dto_1_missing_vote_count.json"), MovieShortDTO.class);
    }
}
