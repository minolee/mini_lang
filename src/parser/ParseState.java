package parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pathm on 2017-08-31.
 */
class ParseState
{
    Map<Keyword, ParseState> shift;
    Map<Keyword, ParseState> reduce;
    ParseState()
    {
        shift = new HashMap<>();
        reduce = new HashMap<>();
    }
    ParseState progress(Keyword keyword)
    {
        return null;
    }
}
