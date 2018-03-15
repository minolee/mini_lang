package parser;

import structure.Keyword;

import java.util.Stack;

class ParseState
{
	private Stack<Partition> partitionStack;
	private Partition current;
	ParseState(Partition initial)
	{
		partitionStack = new Stack<>();
		partitionStack.push(initial);
		current = initial;
	}
	void feed(Keyword k)
	{
		shift(k);
	}
	/**
	 * terminal keyword k를 받아서 shift하는 함수
	 * @param k next keyword
	 */
	private void shift(Keyword k)
	{
		//k는 항상 terminal이어야 함
		current = current.getShift().get(k);
		if(current == null)
		{
			//reduce 어떻게?
			current = partitionStack.pop();
			for(Item i : current.getItems())
			{
				if(i.getNext() == null)
				{
					System.out.println(i);
				}
			}
			return;
		}
		partitionStack.push(current);

	}

	private void reduce(Keyword k)
	{

	}
}