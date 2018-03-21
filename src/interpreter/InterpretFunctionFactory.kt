package interpreter

import structure.Keyword
import util.FunctionFinder

class InterpretFunctionFactory
{
    fun default(k: Keyword)
    {
        for(node in k.children)
            node.interpret()
    }



    fun print_expr(k: Keyword)
    {

    }


    fun Keyword.interpret()
    {
        FunctionFinder.FindInterpretFunctionByName(keyword).invoke(this)
    }
}