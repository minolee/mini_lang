package parser;

import structure.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-09-04.
 * LR(1) item
 */
class Item extends ProductionRule
{
    final int position;
    Item(ProductionRule elem, int pos) //special rule(EOF)를 위해 package-private로 남겨둠
    {
        super(elem);
        position = pos;
    }

    public static List<Item> GenerateItemFromProductionRule(ProductionRule rule)
    {
		List<Item> result = new ArrayList<>();
		for (int i = 0; i <= rule.rhs.size(); i++)
		{
			result.add(new Item(rule, i)); // 맨 마지막까지 들어가야 함
		}
		return result;
    }
}
