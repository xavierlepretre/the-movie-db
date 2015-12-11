package com.github.xavierlepretre.tmdb.model.conf;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ChangeKeyListTest
{
    @Test
    public void joinWith1_isOk() throws Exception
    {
        ChangeKeyList list = new ChangeKeyList();
        list.add(new ChangeKey("key1"));
        assertThat(list.join()).isEqualTo("key1");
    }

    @Test
    public void joinWith2_isOk() throws Exception
    {
        ChangeKeyList list = new ChangeKeyList();
        list.add(new ChangeKey("key1"));
        list.add(new ChangeKey("key2"));
        assertThat(list.join()).isEqualTo("key1,key2");
    }

    @Test
    public void splitWith1_isOk() throws Exception
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ChangeKeyList list = new ChangeKeyList("key1");
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new ChangeKey("key1"));
    }

    @Test
    public void splitWith2_isOk() throws Exception
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ChangeKeyList list = new ChangeKeyList("key1,key2");
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new ChangeKey("key1"));
        assertThat(list.get(1)).isEqualTo(new ChangeKey("key2"));
    }

    @Test
    public void splitWithSpace_doesNotTrim() throws Exception
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ChangeKeyList list = new ChangeKeyList("key1, key2");
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new ChangeKey("key1"));
        assertThat(list.get(1)).isEqualTo(new ChangeKey(" key2"));
    }
}