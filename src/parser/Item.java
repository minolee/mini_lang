package parser;

import structure.Keyword;

/**
 * Created by pathm on 2017-09-04.
 * LR(1) item
 */
public class Item extends ProductionRule
{
    final int position;
    final Keyword lookahead;
    Item(ProductionRule elem, Keyword lookahead) //special rule(EOF)를 위해 package-private로 남겨둠
    {
        super(elem);
        position = 0;
        this.lookahead = lookahead;
    }

    private Item(Item elem)
    {
    	super(elem);

    	position = elem.position + 1;
    	lookahead = elem.lookahead;
    }

    public Item nextItem()
    {
    	return new Item(this);
    }

    public Keyword getNext()
    {
    	return rhs.size() > position ? rhs.get(position) : null;
    }

    @Override
    public String toString()
    {
    	StringBuilder builder = new StringBuilder(generatingKeyword.toString() + " -> ");
	    for (int i = 0; i < rhs.size(); i++)
	    {
	    	if(i == position) builder.append("*");
		    builder.append(rhs.get(i));
	    }
	    if(rhs.size() == position) builder.append("*");
	    return builder.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if( o == null) return false;
        if(!(o instanceof  Item)) return false;
        Item other = ((Item) o);
        return other.generatingKeyword.equals(generatingKeyword) && other.position == position && other.rhs.equals(rhs);
    }
    @Override
	public int hashCode()
    {
    	return (super.hashCode() ^ lookahead.hashCode()) + position;
    }


}
