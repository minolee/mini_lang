package parser;

import structure.Keyword;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pathm on 2017-08-31.
 */
class ParseTableElement
{
    Map<Keyword, ParseTableElement> shift;
    Map<Keyword, ParseTableElement> reduce;
    ParseTableElement()
    {
        shift = new HashMap<>();
        reduce = new HashMap<>();
    }
    ParseTableElement progress(Keyword keyword)
    {
        return null;
    }
}
