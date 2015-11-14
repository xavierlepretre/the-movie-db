package com.github.xavierlepretre.tmdb.model;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class AppendableRequestTest
{
    @Test
    public void isNotEqualToString() throws Exception
    {
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(new AppendableRequest("a_call").equals("a_call")).isFalse();
    }
}
