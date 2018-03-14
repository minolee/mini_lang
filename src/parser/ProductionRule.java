package parser;

import structure.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-09-11.
 * 임시 public
 */
class ProductionRule
{
	private final boolean visible;
	final List<Keyword> rhs;
	final Keyword name;

	ProductionRule(Keyword name, boolean visible, List<Keyword> rhs)
	{
		this.name = name;
		this.visible = visible;
		this.rhs = rhs;
	}

	ProductionRule(ProductionRule elem)
	{
		this.name = elem.name;
		this.visible = elem.visible;
		this.rhs = new ArrayList<>();
		rhs.addAll(elem.rhs);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(" -> ");
		rhs.forEach(builder::append);
		return builder.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!( o instanceof ProductionRule )) return false;
		ProductionRule elem = ( (ProductionRule) o );
		if (rhs.size() != elem.rhs.size()) return false;
		for (int i = 0; i < rhs.size(); i++)
		{
			if(!rhs.get(i).equals(elem.rhs.get(i))) return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		for(Keyword k : rhs)
		{
			result ^= k.hashCode();
		}
		return result;
	}
}
