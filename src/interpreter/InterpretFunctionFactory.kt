package interpreter

import structure.Keyword
import structure.ProgramState
import structure.ProgramValue

class InterpretFunctionFactory
{
    fun default(k: Keyword, context: ProgramState)
    {
        for (node in k.children) node.interpret(context)
    }

    fun if_expr(k: Keyword, context: ProgramState)
    {

    }

    fun print_expr(k: Keyword, context: ProgramState)
    {
        print(k.children[0].interpret(context)!!)
    }

    fun expr(k: Keyword, context: ProgramState): ProgramValue
    {
        return ProgramValue(0.0f)
    }

    fun print_expr(k: Keyword)
    {

    }


}