package structure

import exception.ProgramException

class ProgramNode
{
	//TODO 언젠가 개조하겠지...
	abstract class ScopeContainingKeyword(k: Keyword) : Keyword(k.keyword, k.isTerminal, k.isVisible)
	{
		val scope = HashMap<String, ProgramValue?>()

		init
		{
			k.children.forEach(::addChild)
		}

		open operator fun get(key: String): ProgramValue?
		{
			return scope[key]
		}

		open operator fun contains(key: String) = scope.contains(key)
	}

	class program(k: Keyword) : ScopeContainingKeyword(k)
	{
		val functions = HashMap<String, function_declaration>()
	}

	class function_declaration(k: Keyword) : ScopeContainingKeyword(k)
	{
		val params = HashMap<String, ProgramValue?>()
		val paramNames = ArrayList<String>()

		init
		{
			for (node in k.children)
			{
				if (node.keyword == "ID_LIST")
				{
					node.children.forEach { params[it.strValue!!] = ProgramValue.DummyValue() }
					paramNames.addAll(node.children.map { it.strValue!! })
				}
			}
		}

		fun copy(): function_declaration
		{
			return function_declaration(this)
		}

		fun interpret(vararg inputParams: ProgramValue?): ProgramValue?
		{
			if(inputParams.size != paramNames.size) throw ProgramException(ProgramException.ExceptionType.PARAM_NOT_MATCH)
			for(i in 0..inputParams.size)
			{
				params[paramNames[i]] = inputParams[i]
			}
			return interpret()
		}

		override fun get(key: String): ProgramValue?
		{
			if(scope[key] != null) return scope[key]
			return params[key]
		}

		override fun contains(key: String): Boolean
		{
			return super.contains(key) || params.contains(key)
		}
	}

	class compound_expr(k: Keyword) : ScopeContainingKeyword(k)

}