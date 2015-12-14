package com.github.xavierlepretre.tmdb.model;

import java.util.Collection;
import java.util.HashSet;

public class AppendableRequestSet extends HashSet<AppendableRequest>
{
    public AppendableRequestSet()
    {
        super();
    }

    public AppendableRequestSet(Collection<? extends AppendableRequest> c)
    {
        super(c);
    }

    public AppendableRequestSet(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    @Override public String toString()
    {
        StringBuilder appendToResponse = new StringBuilder();
        String separator = "";
        for (AppendableRequest element : this)
        {
            appendToResponse.append(separator).append(element.toString());
            separator = ",";
        }
        return appendToResponse.toString();
    }

    @Override public final int hashCode()
    {
        int code = 0;
        for (AppendableRequest element : this)
        {
            code ^= element.hashCode();
        }
        return code;
    }

    @Override public final boolean equals(Object o)
    {
        if (!(o instanceof AppendableRequestSet))
        {
            return false;
        }
        for (AppendableRequest element : this)
        {
            if (!((AppendableRequestSet) o).contains(element))
            {
                return false;
            }
        }
        for (AppendableRequest element : (AppendableRequestSet) o)
        {
            if (!contains(element))
            {
                return false;
            }
        }
        return true;
    }
}
