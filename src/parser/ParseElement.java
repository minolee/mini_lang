package parser;

import structure.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-09-11.
 * 임시 public
 */
public class ParseElement
{
	final boolean visible;
	final List<Keyword> rhs;
	Keyword name;

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
		if (!( o instanceof ParseElement )) return false;
		ParseElement elem = ( (ParseElement) o );
		if (rhs.size() != elem.rhs.size()) return false;
		for (int i = 0; i < rhs.size(); i++)
		{
			if(!rhs.get(i).equals(elem.rhs.get(i))) return false;
		}
		return true;
	}
}
