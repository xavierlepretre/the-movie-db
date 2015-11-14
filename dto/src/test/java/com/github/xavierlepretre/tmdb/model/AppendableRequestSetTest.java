package com.github.xavierlepretre.tmdb.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

public class AppendableRequestSetTest
{
    @Test
    public void hashCodeEqualsWhenSameElements() throws Exception
    {
        AppendableRequestSet set1 = new AppendableRequestSet(Arrays.asList(
                new AppendableRequest("a"),
                new AppendableRequest("b")));
        AppendableRequestSet set2 = new AppendableRequestSet(Arrays.asList(
                new AppendableRequest("b"),
                new AppendableRequest("a")));

        assertThat(set1.hashCode()).isEqualTo(set2.hashCode());
        assertThat(set1.equals(set2)).isTrue();
        assertThat(set2.equals(set1)).isTrue();
    }

    @Test
    public void hashCodeEqualsWhenDifferentElements() throws Exception
    {
        AppendableRequestSet set1 = new AppendableRequestSet(Arrays.asList(
                new AppendableRequest("a"),
                new AppendableRequest("b")));
        AppendableRequestSet set2 = new AppendableRequestSet(Arrays.asList(
                new AppendableRequest("b"),
                new AppendableRequest("c")));

        assertThat(set1.hashCode()).isNotEqualTo(set2.hashCode());
        assertThat(set1.equals(set2)).isFalse();
        assertThat(set2.equals(set1)).isFalse();
    }

    @Test
    public void hashCodeEqualsWhenNotSameNumberElements() throws Exception
    {
        AppendableRequestSet set1 = new AppendableRequestSet(Arrays.asList(
                new AppendableRequest("a"),
                new AppendableRequest("b")));
        AppendableRequestSet set2 = new AppendableRequestSet(Collections.singletonList(
                new AppendableRequest("a")));

        assertThat(set1.hashCode()).isNotEqualTo(set2.hashCode());
        assertThat(set1.equals(set2)).isFalse();
        assertThat(set2.equals(set1)).isFalse();
    }
}