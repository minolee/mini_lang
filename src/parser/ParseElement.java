package parser;

import structure.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-09-11.
 */
class ParseElement
{
    Keyword name;
    final boolean visible;
    final List<Keyword> rhs;
    ParseElement(Keyword name, boolean visible, List<Keyword> rhs)
    {
        this.name = name;
        this.visible = visible;
        this.rhs = rhs;
    }

    ParseElement(ParseElement elem)
    {
        this.name = elem.name;
        this.visible = elem.visible;
        this.rhs = new ArrayList<>();
        rhs.addAll(elem.rhs);
    }
}
