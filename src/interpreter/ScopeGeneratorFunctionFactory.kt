package interpreter

import structure.Keyword
import structure.ProgramNode

class ScopeGeneratorFunctionFactory
{
	fun default(k: Keyword)
	{
		k.children.forEach(Keyword::generateScope)
	}

	fun id_declaration(k: Keyword)
	{
		//TODO not working
		k.children.forEach { k.addBoundVariable(it.strValue!!) }
	}

	fun function_declaration(k: Keyword)
	{
		(k.root as ProgramNode.program).functions[k.strValue!!] = k as ProgramNode.function_declaration
	}
}