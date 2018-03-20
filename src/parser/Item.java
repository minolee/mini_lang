package parser;

import structure.Keyword;

/**
 * Created by pathm on 2017-09-04.
 * LR(1) item
 */
public class Item extends ProductionRule
{
	private final int position;
	final Keyword lookahead;
	private Item previousItem;
	private Item nextItem;

	Item(ProductionRule elem, Keyword lookahead) //special rule(EOF)를 위해 package-private로 남겨둠
	{
		super(elem);
		position = 0;
		previousItem = null;
		this.lookahead = lookahead;
	}

	private Item(Item elem) throws Exception
	{
		super(elem);
		this.previousItem = elem;
		position = elem.position + 1;
		if(position > this.rhs.size()) throw new Exception("Over-sized item");
		lookahead = elem.lookahead;
	}

	public Item previousItem()
	{
		return this.previousItem;
	}

	public Item nextItem() throws Exception
	{
		if(this.nextItem == null)
		{
			this.nextItem = new Item(this);
		}
		return this.nextItem;
	}



	public Keyword getNext()
	{
		return rhs.size() > position ? rhs.get(position) : null;
	}

	public Keyword getAfter()
	{
		return rhs.size() > position + 1 ? rhs.get(position + 1) : this.lookahead;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(generatingKeyword.toString() + " -> ");
		for (int i = 0; i < rhs.size(); i++)
		{
			if (i == position) builder.append("*");
			builder.append(rhs.get(i));
		}
		if (rhs.size() == position) builder.append("*");
		builder.append(", ");
		builder.append(lookahead);
		return builder.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null) return false;
		if (!( o instanceof Item )) return false;
		Item other = ( (Item) o );
		return other.generatingKeyword.equals(generatingKeyword) && other.position == position && other.rhs.equals(rhs);
	}

	@Override
	public int hashCode()
	{
		return ( super.hashCode() ^ lookahead.hashCode() ) + position;
	}


}
