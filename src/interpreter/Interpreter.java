package interpreter;

import structure.Keyword;

import java.util.ArrayDeque;
import java.util.Queue;

public class Interpreter
{
	public static Interpreter GenerateInterpreter(Keyword root)
	{
		return new Interpreter(root);
	}

	//fields...가 필요할까?
	private Keyword root;
	private Interpreter(Keyword root)
	{
		this.root = root;
		rebuildTree();
		generateKeywordScope();
	}

	////////////////////////////////////
	//Interpreter generating functions//
	////////////////////////////////////
	//must be private
	private void generateKeywordScope()
	{
		//scope를 keyword에 저장할지, interpreter에 따로 저장할지 생각해 봐야 함
		//scope 추적 함수를 만들어야 하나?
		this.root.generateScope();
	}

	private void rebuildTree()
	{
		//Keyword로만 이루어져 있던 tree를 더 세분화한다

		root = root.rebuild();
		root.setRoot(root);
		Queue<Keyword> queue = new ArrayDeque<>(root.getChildren());
		while(!queue.isEmpty())
		{
			Keyword next = queue.poll();
			next.setRoot(root);
			next = next.rebuild();
			queue.addAll(next.getChildren());
		}
	}

	//////////////////////////
	//Interpreting functions//
	//////////////////////////
	public void interpret()
	{
		root.interpret();
	}
}
