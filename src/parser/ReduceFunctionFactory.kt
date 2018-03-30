package parser

import structure.Keyword
import structure.ProgramValue
import kotlin.collections.ArrayList

class ReduceFunctionFactory
{
	//순서가 매우 중요하므로 무조건 List 사용
	fun default(k: Keyword, context: List<Keyword>)
	{
		context.forEach(k::addChild)
	}

	fun program(k: Keyword, context: List<Keyword>)
	{
		if(context[0].keyword == "ID_DECLARATION") k.addChild(context[0])
		findAndAddAll(context, "LEGAL_SENTENCE").map { it.children[0] }.forEach(k::addChild)
	}
	fun sentences(k: Keyword, context: List<Keyword>) = findAndAddAll(context, "LEGAL_SENTENCE").forEach(k::addChild)
	fun legal_sentence(k: Keyword, context: List<Keyword>) = when (context[0].keyword)
	{
		"COMPOUND_EXPR" -> context[0].children.forEach(k::addChild) //skip COMPOUND_EXPR
		else -> k.addChild(context[0])
	}

	fun id_declaration(k: Keyword, context: List<Keyword>) = id_list(k, context)
	fun id_list(k: Keyword, context: List<Keyword>) = findAndAddAll(context, "ID").forEach(k::addChild)
	fun compound_exprs(k: Keyword, context: List<Keyword>) = findAndAddAll(context, "COMPOUND_EXPR").forEach(k::addChild)
	fun compound_expr(k: Keyword, context: List<Keyword>)
	{
		for(node in context)
		{
			if(node.isTerminal) continue
			else k.addChild(node)
		}
	}
	fun function_declaration(k: Keyword, context: List<Keyword>)
	{
		for (node in context)
		{
			if (node.keyword == "ID")
			{
				k.strValue = node.strValue!!
			}
			if (node.keyword == "ID_LIST")
			{
				node.children.forEach { k.boundVariables.scope[it.strValue!!] = ProgramValue.DummyValue() }
			}
			if (node.keyword == "COMPOUND_EXPR")
			{
				node.children.forEach(k::addChild)
			}
		}
	}

	fun if_expr(k: Keyword, context: List<Keyword>) = case_list(k, context)
	fun do_expr(k: Keyword, context: List<Keyword>) = case_list(k, context)

	fun case_list(k: Keyword, context: List<Keyword>)
	{
		for (node in context)
		{
			findAll(node, "CASE").forEach(k::addChild)
		}
	}

	fun case(k: Keyword, context: List<Keyword>)
	{

		for (node in context)
		{
			if (node.keyword != "THEN") k.addChild(node)
		}

	}

	fun print_expr(k: Keyword, context: List<Keyword>) = findAndAddAll(context, "EXPR").forEach(k::addChild)

	fun base_case(k: Keyword, context: List<Keyword>)
	{
		for (node in context)
		{
			when (node.keyword)
			{
				"EXPR", "ID", "NUMBER" -> k.addChild(node)
			}
		}
	}

	fun expr_list(k: Keyword, context: List<Keyword>) = findAndAddAll(context, "EXPR").forEach(k::addChild)

	fun concurrent_expr(k: Keyword, context: List<Keyword>) = concurrent_expr_(k, context)

	fun concurrent_expr_(k: Keyword, context: List<Keyword>)
	{
		for (node in context)
		{
			when (node.keyword)
			{
				"ID", "EXPR" -> k.addChild(node)
				"CONCURRENT_EXPR_" -> node.children.forEach(k::addChild)
			}
		}
	}

	fun sequence_expr(k: Keyword, context: List<Keyword>) = findAndAddAll(context, "ASSIGN_EXPR").forEach(k::addChild)

	fun assign_expr(k: Keyword, context: List<Keyword>)
	{
		for (node in context)
		{
			when (node.keyword)
			{
				"ID", "EXPR" -> k.addChild(node)
			}
		}
	}


	private fun findAndAddAll(context: List<Keyword>, toAdd: String, iterative: Boolean = false): List<Keyword>
	{
		val result = ArrayList<Keyword>()
		for (c in context) result.addAll(findAll(c, toAdd, iterative))
		return result
	}

	private fun findAll(from: Keyword, keyword: String, iterative: Boolean = false): List<Keyword>
	{
		val result = ArrayList<Keyword>()
		if (from.keyword == keyword)
		{
			result.add(from)
			if (!iterative) return result
		}

		for (node in from.children)
		{
			if (node.keyword == keyword)
			{
				result.add(node)
				if (!iterative) continue
			}
			node.children.forEach { result.addAll(findAll(it, keyword)) }
		}
		return result
	}


}
