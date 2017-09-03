package parser;

import structure.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-09-04.
 * LR(1) item
 */
class Item
{
    final Keyword lhs;
    final List<Keyword> rhs;
    final int position;
    final Keyword lookahead;
    Item(Keyword lhs, int position, Keyword lookahead)
    {
        this.lhs = lhs;
        this.rhs = new ArrayList<>();
        this.position = position;
        this.lookahead = lookahead;
    }
}
