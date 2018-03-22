package interpreter;

import structure.Keyword;
import structure.ProgramState;

public class Interpreter
{
	public static Interpreter GenerateInterpreter(Keyword root)
	{
		return new Interpreter(root);
	}

	//fields...가 필요할까?
	private final Keyword root;
	private Interpreter(Keyword root)
	{
		this.root = root;
	}

	////////////////////////////////////
	//Interpreter generating functions//
	////////////////////////////////////
	//must be private
	private void generateKeywordScope()
	{
		//scope를 keyword에 저장할지, interpreter에 따로 저장할지 생각해 봐야 함
		//scope 추적 함수를 만들어야 하나?
		root.getChildren();
	}

	//////////////////////////
	//Interpreting functions//
	//////////////////////////
	public void interpret()
	{
		root.interpret(new ProgramState());
	}


}
