package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParameterColumnValue
{
    @NonNull public final List<ColumnValue> rowValues;
    @NonNull public final List<String> columns;
    @NonNull public final List<List<String>> rows;

    public ParameterColumnValue(@NonNull List<ColumnValue> rowValues)
    {
        this.rowValues = Collections.unmodifiableList(rowValues);
        List<String> columns = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        if (rowValues.size() > 0)
        {
            for (int i = 0; i < rowValues.get(0).values.size(); i++)
            {
                rows.add(new ArrayList<String>());
            }
        }
        for (ColumnValue rowValue : rowValues)
        {
            columns.add(rowValue.column);
            for (int i = 0; i < rowValue.values.size(); i++)
            {
                rows.get(i).add(rowValue.values.get(i));
            }
        }
        this.columns = Collections.unmodifiableList(columns);
        List<List<String>> unmodifiableRows = new ArrayList<>();
        for (List<String> row : rows)
        {
            unmodifiableRows.add(Collections.unmodifiableList(row));
        }
        this.rows = Collections.unmodifiableList(unmodifiableRows);
    }

    /**
     * @param potentialColumnsAndValues the secondary array must have a length of 2 or more. And they must all have the same length.
     * @return
     */
    @NonNull public static ParameterColumnValue[] getPossibleParameters(
            @NonNull String[][] potentialColumnsAndValues)
    {
        List<List<String[]>> collections = new CollectionHelper().getAllSubCollections(potentialColumnsAndValues);
        ParameterColumnValue[] parameters = new ParameterColumnValue[collections.size()];
        int i = 0;
        int pairLength = 0;
        for (List<String[]> collection : collections)
        {
            List<ColumnValue> columnValues = new ArrayList<>();
            for (String[] pair : collection)
            {
                if (pair.length < 2)
                {
                    throw new IllegalArgumentException("There needs to be at least 1 value");
                }
                if (pairLength == 0)
                {
                    pairLength = pair.length;
                }
                else if (pair.length != pairLength)
                {
                    throw new IllegalArgumentException("Not all pairs have the same length " + pair.length + " and " + pairLength);
                }
                String[] rowValues = new String[pairLength - 1];
                System.arraycopy(pair, 1, rowValues, 0, pairLength - 1);
                ColumnValue columnValue = new ColumnValue(pair[0], rowValues);
                columnValues.add(columnValue);
            }
            parameters[i++] = new ParameterColumnValue(columnValues);
        }
        return parameters;
    }

    public static final class ColumnValue
    {
        @NonNull public final String column;
        // Minimum length of 1
        @NonNull public final List<String> values;

        public ColumnValue(@NonNull String column, @NonNull String[] values)
        {
            this.column = column;
            this.values = Collections.unmodifiableList(Arrays.asList(values));
        }

        @Override public boolean equals(Object o)
        {
            boolean isEqual = (o instanceof ColumnValue)
                    && ((ColumnValue) o).column.equals(column)
                    && ((ColumnValue) o).values.size() == values.size();
            if (!isEqual)
            {
                return false;
            }
            for (String value : values)
            {
                isEqual &= ((ColumnValue) o).values.contains(value);
            }
            return isEqual;
        }

        @Override public int hashCode()
        {
            int hash = column.hashCode();
            for (String value : values)
            {
                hash ^= value.hashCode();
            }
            return hash;
        }
    }
}
