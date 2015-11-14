package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrewDTO extends PersonDTO
{
    @NonNull private final String department;
    @NonNull private final String job;

    public CrewDTO(
            @JsonProperty(value = "credit_id", required = true) @NonNull
            CreditId creditId,
            @JsonProperty(value = "department", required = true) @NonNull
            String department,
            @JsonProperty(value = "id", required = true) @NonNull
            PersonId id,
            @JsonProperty(value = "job", required = true) @NonNull
            String job,
            @JsonProperty(value = "name", required = true) @NonNull
            String name,
            @JsonProperty(value = "profile_path", required = true) @NonNull
            String profilePath)
    {
        super(creditId, id, name, profilePath);
        this.department = department;
        this.job = job;
    }

    @NonNull public String getDepartment()
    {
        return department;
    }

    @NonNull public String getJob()
    {
        return job;
    }
}
