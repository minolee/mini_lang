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
	private final Keyword generatingKeyword;

	ProductionRule(Keyword generatingKeyword, boolean visible, List<Keyword> rhs)
	{
		this.generatingKeyword = generatingKeyword;
		this.visible = visible;
		this.rhs = rhs;
	}

	ProductionRule(ProductionRule elem)
	{
		this.generatingKeyword = elem.generatingKeyword;
		this.visible = elem.visible;
		this.rhs = new ArrayList<>();
		rhs.addAll(elem.rhs);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(generatingKeyword);
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
		int result = generatingKeyword.hashCode();
		for(Keyword k : rhs)
		{
			result ^= k.hashCode();
		}
		return result;
	}

	public Keyword getGeneratingKeyword()
	{
		return new Keyword(generatingKeyword);
	}
}
