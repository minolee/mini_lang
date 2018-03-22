package parser

import structure.Keyword
import java.util.*

class ParseFunctionFactory
{
    //순서가 매우 중요하므로 무조건 List 사용
    fun default(context: List<Keyword>): List<Keyword>
    {
        val temp = ArrayList<Keyword>()
        context.forEach { temp.add(it) }
        return temp
    }
    fun program(context: List<Keyword>) = findAndAddAll(context, "LEGAL_SENTENCE").map{it.children[0]}.toList()
    fun sentences(context: List<Keyword>) = findAndAddAll(context, "LEGAL_SENTENCE")
    fun legal_sentence(context: List<Keyword>): List<Keyword>
    {
        val result: List<Keyword>
        when(context[0].keyword)
        {
            "DO_EXPR", "IF_EXPR" -> result = Collections.singletonList(context[0])
            else -> result =  Collections.singletonList(context[0].children[0]) //skip COMPOUND_EXPR
        }
        return result
    }
    fun if_expr(context: List<Keyword>) = case_list(context)
    fun do_expr(context: List<Keyword>) = case_list(context)

    fun case_list(context: List<Keyword>): List<Keyword>
    {
        val temp = ArrayList<Keyword>()
        for (node in context)
        {
            temp.addAll(findAll(node, "CASE"))
        }
        return temp
    }

    fun case(context: List<Keyword>): List<Keyword> = findAndAddAll(context, "EXPR")

    fun print_expr(context: List<Keyword>) = findAndAddAll(context, "EXPR")

    fun base_case(context: List<Keyword>): List<Keyword>
    {
        val result = ArrayList<Keyword>()
        for (node in context)
        {
            when (node.keyword)
            {
                "EXPR", "ID", "NUMBER" -> result.add(node)
            }
        }
        return result
    }

    fun concurrent_expr(context: List<Keyword>) = concurrent_expr_(context)

    fun concurrent_expr_(context: List<Keyword>): List<Keyword>
    {
        val result = ArrayList<Keyword>()
        for (node in context)
        {
            when (node.keyword)
            {
                "ID", "EXPR" -> result.add(node)
                "CONCURRENT_EXPR_" -> result.addAll(node.children)
            }
        }
        return result
    }

    fun sequence_expr(context: List<Keyword>) = findAndAddAll(context, "ASSIGN_EXPR")

    fun assign_expr(context: List<Keyword>): List<Keyword>
    {
        val result = ArrayList<Keyword>()
        for (node in context)
        {
            when (node.keyword)
            {
                "ID", "EXPR" -> result.add(node)
            }
        }
        return result
    }


    private fun findAndAddAll(context: List<Keyword>, toAdd: String, iterative: Boolean = false): List<Keyword>
    {
        val result = ArrayList<Keyword>()
        for(c in context)
            result.addAll(findAll(c, toAdd, iterative))
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

