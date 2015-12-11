package com.github.xavierlepretre.tmdb.model.conf;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ImageSizeListTest
{
    @Test
    public void joinWith1_isOk() throws Exception
    {
        ImageSizeList list = new ImageSizeList();
        list.add(new ImageSize("w150"));
        assertThat(list.join()).isEqualTo("w150");
    }

    @Test
    public void joinWith2_isOk() throws Exception
    {
        ImageSizeList list = new ImageSizeList();
        list.add(new ImageSize("w150"));
        list.add(new ImageSize("w300"));
        assertThat(list.join()).isEqualTo("w150,w300");
    }

    @Test
    public void splitWith1_isOk() throws Exception
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ImageSizeList list = new ImageSizeList("w150");
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new ImageSize("w150"));
    }

    @Test
    public void splitWith2_isOk() throws Exception
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ImageSizeList list = new ImageSizeList("w150,w300");
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new ImageSize("w150"));
        assertThat(list.get(1)).isEqualTo(new ImageSize("w300"));
    }

    @Test
    public void splitWithSpace_doesNotTrim() throws Exception
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ImageSizeList list = new ImageSizeList("w150, w300");
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(new ImageSize("w150"));
        assertThat(list.get(1)).isEqualTo(new ImageSize(" w300"));
    }
}