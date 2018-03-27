package interpreter

import exception.ProgramException
import structure.Keyword
import structure.ProgramScope
import structure.ProgramValue
import java.util.*

class InterpretFunctionFactory
{
	companion object
	{
		val randomSeed = Random()
	}

	fun default(k: Keyword)
	{
		for (node in k.children) node.interpret()
	}

	fun if_expr(k: Keyword): ProgramValue?
	{
		val trueList = ArrayList<Keyword>()
		for (node in k.children)
		{
			if (node.children[0].interpret()?.value == 1) trueList.add(node)
		}
		if (trueList.size == 0) throw ProgramException(ProgramException.ExceptionType.ABORT)
		return trueList[randomSeed.nextInt(trueList.size)].children[1].interpret()
	}

	fun do_expr(k: Keyword): ProgramValue?
	{
		try
		{
			while (true)
			{
				if_expr(k)
			}
		}
		catch (e: ProgramException)
		{

		}
		return null
	}

	fun print_expr(k: Keyword)
	{
		println(k.children[0].interpret()!!)
	}

	fun expr(k: Keyword): ProgramValue = pass(k)
	fun expr6(k: Keyword): ProgramValue = pass(k)
	fun expr8(k: Keyword): ProgramValue = pass(k)
	fun expr9(k: Keyword): ProgramValue = pass(k)
	fun expr10(k: Keyword): ProgramValue
	{
		val value = k.children[k.children.size - 1].interpret()
		return if (k.children.size > 1) -value!! else value!!
	}

	fun base_case(k: Keyword): ProgramValue = when (k.children[0].keyword)
	{
		"EXPR" -> expr(k)
		"ID" -> findId(k.children[0])!!
		"NUMBER" -> if (k.children[0].keywordType == "Int") ProgramValue(k.children[0].intValue!!) else ProgramValue(k.children[0].floatValue!!)
		else -> throw Exception("base_case: ${k.children[0].keyword}")
	}

	fun assign_expr(k: Keyword)
	{
		//함수가 없으니까 일단 이렇게 해도 됨
		modifyVariable(k.children[0], k.children[1].interpret()!!)
	}

	//[EXPR][EXPRX_] 형태의 EXPR
	fun pass(k: Keyword): ProgramValue
	{
		val left = k.children[0].interpret()!! // base case 때문에 pass를 못 부름
		if (k.children.size > 1) return expr_underbar(k.children[1], left)
		return left
	}

	//EXPR_ 형태
	fun expr_underbar(k: Keyword, left: ProgramValue): ProgramValue
	{
		val op = k.children[0].keyword
		val right = k.children[1].interpret()!!
		//3번째 element도 고려해야 해!
		val x: ProgramValue = when (op)
		{
			"EQ" -> ProgramValue(if (left == right) 1 else 0)
			"NE" -> ProgramValue(if (left != right) 1 else 0)
			"GT" -> ProgramValue(if (left > right) 1 else 0)
			"LT" -> ProgramValue(if (left < right) 1 else 0)
			"GTE" -> ProgramValue(if (left >= right) 1 else 0)
			"LTE" -> ProgramValue(if (left <= right) 1 else 0)
			"PLUS" -> left + right
			"MINUS" -> left - right
			"MULTIPLY" -> left * right
			"DIVIDE" -> left / right
			"REMAINDER" -> left % right
			else -> throw Exception("Invalid operator: $op")
		}
		if (k.children.size > 2)
		{
			return expr_underbar(k.children[2], x)
		}
		return x
	}

	fun findId(k: Keyword): ProgramValue?
	{
		var current = k.boundVariables
		while (current.parent != current)
		{
			if (current.scope[k.strValue!!] != null) return current.scope[k.strValue!!]
			current = current.parent
		}
		return null
	}

	fun modifyVariable(k: Keyword, v: ProgramValue)
	{
		var current = k.boundVariables
		while (current.parent != current)
		{
			if (current.scope[k.strValue!!] != null) current.scope[k.strValue!!] = v
			current = current.parent
		}
	}
}