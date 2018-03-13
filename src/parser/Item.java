package parser;

import structure.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-09-04.
 * LR(1) item
 */
class Item extends ParseElement
{

    final int position;
    final Keyword lookahead;
    Item(ParseElement elem, int pos, Keyword lookahead)
    {
        super(elem);
        position = pos;
        this.lookahead = lookahead;
    }


}
