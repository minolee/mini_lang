package parser

import structure.Keyword

class ParseFunctionFactory
{

    fun Default(context: List<Keyword>): List<Keyword>
    {
        val temp = ArrayList<Keyword>()
        context.forEach { temp.add(it) }
        return temp
    }

    fun Case_List(context: List<Keyword>): List<Keyword>
    {
        val temp = ArrayList<Keyword>()
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

    private fun Keyword.hasParent(target: String): Boolean
    {
        if (parent == null) return false
        if (keyword == target) return true
        return parent!!.hasParent(target)
    }


}

