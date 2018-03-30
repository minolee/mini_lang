package interpreter

import structure.Keyword
import structure.ProgramValue

class ScopeGeneratorFunctionFactory
{
	fun default(k: Keyword)
	{
		k.children.forEach(Keyword::generateScope)
	}

	fun id_declaration(k: Keyword)
	{
		//TODO not working
		k.children.forEach { k.parent!!.boundVariables.scope[it.strValue!!] = ProgramValue.DummyValue() }
	}

	fun function_declaration(k: Keyword)
	{
		//k.root.boundVariables.scope[k.strValue!!] = ProgramValue() //TODO where to store function structure
	}
}