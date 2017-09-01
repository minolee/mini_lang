package structure;

import lombok.Getter;

/**
 * Created by pathm on 2017-08-31.
 */
public class Keyword
{
    @Getter
    final String keyword;
    @Getter
    final boolean isTerminal;
    public Keyword(String keyword, boolean isTerminal)
    {
        this.keyword = keyword;
        this.isTerminal = isTerminal;
    }

    @Override
    public String toString()
    {
        return "<"+keyword+">";
    }
}
