package parser

import structure.Keyword

class ParseFunctionFactory
{
    private val DefaultArrayList = { ArrayList<Keyword>() } //개귀찮

    fun Default(context: List<Keyword>): List<Keyword>
    {
        val temp = DefaultArrayList()
        context.forEach { temp.add(it) }
        return temp
    }

    fun Sentence(context: List<Keyword>): List<Keyword>
    {
        val temp = DefaultArrayList()
        
        return temp
    }

    fun Case_List(context: List<Keyword>): List<Keyword>
    {
        val temp = DefaultArrayList()
        for (node in context)
        {
            if (node.keyword == "CASE") temp.add(node)
            else
            {
                temp.addAll(Case_List(node.children[1].children))
            }
        }
        return temp
    }


    private fun findAll(from: Keyword, keyword: String): List<Keyword>
    {
        val result = ArrayList<Keyword>()
        for (node in from.children)
        {
            if (node.keyword == keyword) result.add(node)
            node.children.forEach { result.addAll(findAll(it, keyword)) }
        }
        return result
    }

    private fun Keyword.hasParent(target: String, until: Keyword? = null): Boolean
    {
        if (parent == null) return false
        if (keyword == target) return true
        return parent!!.hasParent(target)
    }


}

