package parser

import structure.Keyword
import structure.ProgramNode

/**
 *  AST를 print하기 위해 있는 class
 *  Node에 특수한 print rule이 필요한 경우를 위해 만들어 둠
 *  역시 reflection을 통해 call한다.*/

class PrintFunctionFactory
{
	fun default(k: Keyword, tabs: Int)
	{
		if (k is ProgramNode.function_declaration) return
		val prefix = StringBuilder()
		for (i in 0..tabs) prefix.append("\t")
		if (k.surface == "")
		{
			k.children.forEach {
				it.printAST(tabs)
			}
			return
		}

		print(prefix.toString())
		if (k.children.size > 0) print("(")
		println(k.surface)
		var iterCount = 0
		for (child in k.children)
		{
			child.printAST(tabs + 1)
			if (iterCount < k.children.size - 1) println("$prefix\t,")
			iterCount++
		}
		if (k.children.size > 0) println(prefix.toString() + ")")
	}

	fun id_declaration(k: Keyword, tabs: Int){}

	fun concurrent_expr(k: Keyword, tabs: Int)
	{
		val prefix = StringBuilder()
		for (i in 0..tabs) prefix.append("\t")
		print("$prefix(")
		println(":=")
		//TODO 하나의 pair마다 할건지, 한번에 몰아서 보여줄건지 결정해야 함
		val lhs = k.children.subList(0, k.children.size / 2)
		val rhs = k.children.subList(k.children.size / 2, k.children.size)
		for(i in 0 until lhs.size)
		{
			println("$prefix\t(")
			lhs[i].printAST(tabs+2)
			println("$prefix\t\t,")
			rhs[i].printAST(tabs+2)
			println("$prefix\t)")
		}
		println("$prefix)")
	}



	fun case(k:Keyword, tabs: Int)
	{
		val prefix = StringBuilder()
		for (i in 0..tabs) prefix.append("\t")
		println("$prefix(")
		k.children[0].printAST(tabs+1)
		println("$prefix->")
		k.children[1].printAST(tabs+1)
		println("$prefix)")
	}
}