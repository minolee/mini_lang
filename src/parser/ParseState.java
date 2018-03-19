package parser;

import structure.Keyword;

import java.util.Stack;

class ParseState
{
	private Stack<Closure> closureStack;
	private Closure current;
	ParseState(Closure initial)
	{
		closureStack = new Stack<>();
		closureStack.push(initial);
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
			current = closureStack.pop();
			for(Item i : current.getItems())
			{
				if(i.getNext() == null)
				{
					System.out.println(i);
				}
			}
			return;
		}
		closureStack.push(current);

	}

	private void reduce(Keyword k)
	{

	}
}