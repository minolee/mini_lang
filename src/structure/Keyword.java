package structure;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by pathm on 2017-08-31.
 */
public class Keyword<T>
{
    @Getter
    final String keyword;
    @Getter
    final boolean isTerminal;
    @Setter@Getter
    T data;
    @Setter
    @Getter
    boolean visible;

    public static final Keyword EOF = new Keyword("eof", true);

    public Keyword(String keyword, boolean isTerminal)
    {
        this.keyword = keyword;
        this.isTerminal = isTerminal;
    }

    public Keyword(String keyword, boolean isTerminal, T data)
    {
        this(keyword, isTerminal);
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
