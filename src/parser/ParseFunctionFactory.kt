package parser

import structure.Keyword

class ParseFunctionFactory
{
    private val defaultKeywordArray = { ArrayList<Keyword>() } //개귀찮
    //순서가 매우 중요하므로 무조건 List 사용
    fun default(context: List<Keyword>): List<Keyword>
    {
        val temp = defaultKeywordArray()
        context.forEach { temp.add(it) }
        return temp
    }

    fun sentence(context: List<Keyword>): List<Keyword>
    {
        val temp = defaultKeywordArray()
        for (node in context)
        {
            temp.addAll(findAll(node, "LEGAL_SENTENCE"))
        }
        return temp
    }

    fun if_expr(context: List<Keyword>): List<Keyword> = case_list(context)
    fun do_expr(context: List<Keyword>): List<Keyword> = case_list(context)

    fun case_list(context: List<Keyword>): List<Keyword>
    {
        val temp = defaultKeywordArray()
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
        val result = defaultKeywordArray()
        for(node in context)
        {
            when(node.keyword)
            {
                "EXPR", "ID", "NUMBER" -> result.add(node)
            }
        }
        return result
    }

    fun concurrent_expr(context: List<Keyword>) = concurrent_expr_(context)

    fun concurrent_expr_(context: List<Keyword>): List<Keyword>
    {
        val result = defaultKeywordArray()
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
        val result = defaultKeywordArray()
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
        return context.fold(defaultKeywordArray(), { list, keyword -> list.addAll(findAll(keyword, toAdd, iterative)); list })
    }

    private fun findAll(from: Keyword, keyword: String, iterative: Boolean = false): List<Keyword>
    {
        val result = ArrayList<Keyword>()
        for (node in from.children)
        {
            if (node.keyword == keyword) result.add(node)
            if (!iterative) continue
            node.children.forEach { result.addAll(findAll(it, keyword)) }
        }
        return result
    }


}

