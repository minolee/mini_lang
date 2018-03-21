package interpreter

import structure.Keyword
import util.FunctionFinder

class InterpretFunctionFactory
{

    fun Keyword.invoke()
    {
        FunctionFinder.FindInterpretFunctionByName(keyword).invoke(this)
    }
}