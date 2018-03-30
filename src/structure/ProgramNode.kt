package structure

class ProgramNode
{
	//TODO 언젠가 개조하겠지...
	abstract class ScopeContainingKeyword(k: Keyword): Keyword(k.keyword, k.isTerminal, k.isVisible)
	{
		val scope = ProgramScope()
		init
		{
			k.children.forEach(::addChild)
		}
	}

	class program(k: Keyword): ScopeContainingKeyword(k)
	{
		val functions = HashMap<String, ProgramFunction>()
	}

	class compound_expr(k: Keyword): ScopeContainingKeyword(k)
	{

	}


}