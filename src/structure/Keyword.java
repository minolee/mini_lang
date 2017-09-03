package structure;

import lombok.Getter;

/**
 * Created by pathm on 2017-08-31.
 */
public class Keyword<T>
{
    @Getter
    final String keyword;
    @Getter
    final boolean isTerminal;
    final T data;
    public Keyword(String keyword, boolean isTerminal, T data)
    {
        this.keyword = keyword;
        this.isTerminal = isTerminal;
        this.data = data;
    }

    @Override
    public String toString()
    {
        if(isTerminal)
            return "<"+keyword+">";
        return "["+keyword+"]";
    }
}
