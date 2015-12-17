package com.github.xavierlepretre.tmdb.sql;

import com.github.xavierlepretre.tmdb.sql.ParameterColumnValue.ColumnValue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

public class ParameterColumnValueTest
{
    @Test(expected = IllegalArgumentException.class)
    public void getPossibleParametersWith1Inner_fails() throws Exception
    {
        ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a"}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPossibleParametersWithDecreasingPairLength_fails() throws Exception
    {
        ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a", "a1"},
                new String[]{"b", "b1", "b2"}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPossibleParametersWithIncreasingPairLength_fails() throws Exception
    {
        ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a", "a1", "a2"},
                new String[]{"b", "b1"}
        });
    }

    @Test
    public void getPossibleParametersWith1Column1Row() throws Exception
    {
        ParameterColumnValue[] values = ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a", "a1"}
        });
        assertThat(values.length).isEqualTo(2);
        assertThat(values[0].rowValues).isEmpty();
        assertThat(values[0].columns).isEmpty();
        assertThat(values[0].rows).isEmpty();
        assertThat(values[1].rowValues).containsOnly(new ColumnValue("a", new String[]{"a1"}));
        assertThat(values[1].columns).containsOnly("a");
        //noinspection unchecked
        assertThat(values[1].rows).containsOnly(Collections.singletonList("a1"));
    }

    @Test
    public void getPossibleParametersWith1Column2Rows() throws Exception
    {
        ParameterColumnValue[] values = ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a", "a1", "a2"}
        });
        assertThat(values.length).isEqualTo(2);
        assertThat(values[0].rowValues).isEmpty();
        assertThat(values[0].columns).isEmpty();
        assertThat(values[0].rows).isEmpty();
        assertThat(values[1].rowValues).containsOnly(new ColumnValue("a", new String[]{"a1", "a2"}));
        assertThat(values[1].columns).containsOnly("a");
        //noinspection unchecked
        assertThat(values[1].rows).containsOnly(Collections.singletonList("a1"), Collections.singletonList("a2"));
    }

    @Test
    public void getPossibleParametersWith2Columns1Row() throws Exception
    {
        ParameterColumnValue[] values = ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a", "a1"},
                new String[]{"b", "b1"}
        });
        assertThat(values.length).isEqualTo(4);
        assertThat(values[0].rowValues).isEmpty();
        assertThat(values[0].columns).isEmpty();
        assertThat(values[0].rows).isEmpty();
        assertThat(values[1].rowValues).containsOnly(new ColumnValue("b", new String[]{"b1"}));
        assertThat(values[1].columns).containsOnly("b");
        //noinspection unchecked
        assertThat(values[1].rows).containsOnly(Collections.singletonList("b1"));
        assertThat(values[2].rowValues).containsOnly(new ColumnValue("a", new String[]{"a1"}));
        assertThat(values[2].columns).containsOnly("a");
        //noinspection unchecked
        assertThat(values[2].rows).containsOnly(Collections.singletonList("a1"));
        assertThat(values[3].rowValues).containsOnly(
                new ColumnValue("a", new String[]{"a1"}),
                new ColumnValue("b", new String[]{"b1"}));
        assertThat(values[3].columns).containsOnly("a", "b");
        //noinspection unchecked
        assertThat(values[3].rows).containsOnly(
                Arrays.asList("a1", "b1"));
    }

    @Test
    public void getPossibleParametersWith2Columns2Rows() throws Exception
    {
        ParameterColumnValue[] values = ParameterColumnValue.getPossibleParameters(new String[][]{
                new String[]{"a", "a1", "a2"},
                new String[]{"b", "b1", "b2"}
        });
        assertThat(values.length).isEqualTo(4);
        assertThat(values[0].rowValues).isEmpty();
        assertThat(values[0].columns).isEmpty();
        assertThat(values[0].rows).isEmpty();
        assertThat(values[1].rowValues).containsOnly(new ColumnValue("b", new String[]{"b1", "b2"}));
        assertThat(values[1].columns).containsOnly("b");
        //noinspection unchecked
        assertThat(values[1].rows).containsOnly(Collections.singletonList("b1"), Collections.singletonList("b2"));
        assertThat(values[2].rowValues).containsOnly(new ColumnValue("a", new String[]{"a1", "a2"}));
        assertThat(values[2].columns).containsOnly("a");
        //noinspection unchecked
        assertThat(values[2].rows).containsOnly(Collections.singletonList("a1"), Collections.singletonList("a2"));
        assertThat(values[3].rowValues).containsOnly(
                new ColumnValue("a", new String[]{"a1", "a2"}),
                new ColumnValue("b", new String[]{"b1", "b2"}));
        assertThat(values[3].columns).containsOnly("a", "b");
        //noinspection unchecked
        assertThat(values[3].rows).containsOnly(
                Arrays.asList("a1", "b1"),
                Arrays.asList("a2", "b2"));
    }
}