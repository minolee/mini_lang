package interpreter

import exception.ProgramException
import structure.Keyword
import structure.ProgramNode
import structure.ProgramValue
import java.util.*
import kotlin.collections.HashMap

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

	fun program(k: Keyword)
	{
		for (node in k.children)
		{
			if (node is ProgramNode.function_declaration) continue
			node.interpret()
		}
	}

	fun function_declaration(k: Keyword)
	{
		for (node in k.children)
		{
			if (node.keyword == "ID_LIST") continue
			node.interpret()
		}
	}

	fun abort(k: Keyword): Nothing = throw ProgramException(ProgramException.ExceptionType.ABORT)

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
		catch (e: ProgramException) //catch ABORT
		{
			if (e.type == ProgramException.ExceptionType.ABORT) return null
			else throw e
			//true가 없다 -> skip
		}
	}

	fun concurrent_expr(k: Keyword)
	{
		val values = k.children.filter { it.keyword == "EXPR" }.map(Keyword::interpret)
		val ids = k.children.filter { it.keyword == "ID" }
		for (i in 0 until values.size)
		{
			modifyVariable(ids[i], values[i])
		}
	}

	fun print_expr(k: Keyword)
	{
		println(k.children[0].interpret()!!)
	}

	fun expr(k: Keyword): ProgramValue? = pass(k)
	fun expr6(k: Keyword): ProgramValue? = pass(k)
	fun expr8(k: Keyword): ProgramValue? = pass(k)
	fun expr9(k: Keyword): ProgramValue? = pass(k)
	fun expr10(k: Keyword): ProgramValue?
	{
		val value = k.children[k.children.size - 1].interpret()
		return if (k.children.size > 1) -value!! else value
	}

	fun base_case(k: Keyword): ProgramValue? = when (k.children[0].keyword)
	{
		"EXPR" -> expr(k)
		"ID" -> findId(k.children[0])
				?: throw ProgramException(ProgramException.ExceptionType.FREE_VARIABLE, k.children[0].strValue!!)
		"NUMBER" -> if (k.children[0].keywordType == "Int") ProgramValue(k.children[0].intValue!!) else ProgramValue(k.children[0].floatValue!!)
		"INVOKE_EXPR" -> k.children[0].interpret()
		else -> throw Exception("base_case: ${k.children[0].keyword}")
	}

	fun assign_expr(k: Keyword) = modifyVariable(k.children[0], k.children[1].interpret()!!)


	fun invoke_expr(k: Keyword): ProgramValue?
	{
		val function = (k.root as ProgramNode.program).functions[k.strValue!!]
				?: throw ProgramException(ProgramException.ExceptionType.NO_FUNCTION)
		val args = k.children.map { it.interpret() }.toList()
		return function.copy().interpret(args)
	}

	fun return_expr(k: Keyword): Nothing
	{
		val ex = ProgramException(ProgramException.ExceptionType.RETURN)
		val result = k.children[1].interpret()
		ex.returnValue = result
		throw ex //거의 longjmp
	}

	//sub functions
	//[EXPR][EXPRX_] 형태의 EXPR
	fun pass(k: Keyword): ProgramValue?
	{
		val left = k.children[0].interpret() // base case 때문에 pass를 못 부름
		if (k.children.size > 1) return expr_underbar(k.children[1], left!!) // null-return 함수의 결과값과 다른 programvalue를 더할 수 없음
		return left
	}

	//EXPR_ 형태
	fun expr_underbar(k: Keyword, left: ProgramValue): ProgramValue?
	{
		val op = k.children[0].keyword
		val right = k.children[1].interpret()!!
		if(!left.declared || !right.declared) throw Exception("Undeclared")
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

	fun findId(target: Keyword, source: Keyword = target): ProgramValue?
	{
		if (source !is ProgramNode.ScopeContainingKeyword) return findId(target, source.parent)
		if (target.strValue!! !in source)
		{
			if (source.parent == source) return null
			return findId(target, source.parent)
		}
		return source[target.strValue!!]
	}

	fun modifyVariable(k: Keyword, v: ProgramValue?)
	{
		var current = k
		do
		{
			current = current.parent
			if (current !is ProgramNode.ScopeContainingKeyword) continue
			if (current[k.strValue!!] != null)
			{
				current[k.strValue!!] = v
				break
			}
		}
		while (current.parent != current)
	}
}