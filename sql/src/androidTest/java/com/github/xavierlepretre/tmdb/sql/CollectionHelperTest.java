package com.github.xavierlepretre.tmdb.sql;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class CollectionHelperTest
{
    @Test
    public void getCollectionsOn0() throws Exception
    {
        assertThat(new CollectionHelper().getAllSubCollections(new String[0])).isEmpty();
    }

    @Test
    public void getCollectionsOn1() throws Exception
    {
        List<List<String>> all = new CollectionHelper().getAllSubCollections(new String[]{"a"});
        //noinspection unchecked
        assertThat(all).containsOnly(
                Collections.<String>emptyList(),
                Collections.singletonList("a"));
    }

    @Test
    public void getCollectionsOn2() throws Exception
    {
        List<List<String>> all = new CollectionHelper().getAllSubCollections(new String[]{"a", "b"});
        //noinspection unchecked
        assertThat(all).containsOnly(
                Collections.<String>emptyList(),
                Collections.singletonList("a"),
                Collections.singletonList("b"),
                Arrays.asList("a", "b"));
    }

    @Test
    public void getCollectionsOn3() throws Exception
    {
        List<List<String>> all = new CollectionHelper().getAllSubCollections(new String[]{"a", "b", "c"});
        //noinspection unchecked
        assertThat(all).containsOnly(
                Collections.<String>emptyList(),
                Collections.singletonList("c"),
                Collections.singletonList("b"),
                Arrays.asList("b", "c"),
                Collections.singletonList("a"),
                Arrays.asList("a", "c"),
                Arrays.asList("a", "b"),
                Arrays.asList("a", "b", "c"));
    }
}