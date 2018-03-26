package structure

import interpreter.InterpretFunctionFactory
import util.FunctionFinder
import java.lang.reflect.Method

class Keyword
{

    constructor(keyword: String, isTerminal: Boolean, isVisible: Boolean)
    {
        this.keyword = keyword
        this.isTerminal = isTerminal
        this.isVisible = isVisible
        this.reduceFun = FunctionFinder.FindParseFunctionByName(keyword.toLowerCase())
        this.interpretFun = FunctionFinder.FindInterpretFunctionByName(keyword.toLowerCase())
    }

    constructor(keyword: String, isTerminal: Boolean) : this(keyword, isTerminal, true)
    constructor(copy: Keyword) : this(copy.keyword, copy.isTerminal, copy.isVisible)


    val keyword: String
    val isTerminal: Boolean
    val isVisible: Boolean
    var strValue: String? = null
    var intValue: Int? = null
    var floatValue: Float? = null
    var keywordType: String = "None"
    var assigned: Boolean = false
    var parent: Keyword? = null
    val children = ArrayList<Keyword>()
    private val interpretFun: Method
    val reduceFun: Method
    //local variable 준비
    val boundVariables = ProgramScope()
    //leaf부터 시작해서 올라오면서 이 flag가 true인 keyword node를 만난다면 이 keyword는 이 keyword에 bound된 로컬 variable인거임
    var boundVariableStopHere = false

    var original: String? = null

    companion object
    {
        @JvmField
        val EOF = Keyword("eof", true)
        @JvmField
        val EPSILON = Keyword("epsilon", true)
        val InterpreterFactoryObject = InterpretFunctionFactory()
    }

    constructor(keyword: String, isTerminal: Boolean, value: String) : this(keyword, isTerminal)
    {
        strValue = value
        keywordType = "String"
    }

    constructor(keyword: String, isTerminal: Boolean, value: Int) : this(keyword, isTerminal)
    {
        intValue = value
        keywordType = "Int"
    }

    constructor(keyword: String, isTerminal: Boolean, value: Float) : this(keyword, isTerminal)
    {
        floatValue = value
        keywordType = "Float"
    }

    override fun toString(): String
    {
        val open = if (isTerminal) "<" else "["
        val close = if (isTerminal) ">" else "]"
        return "$open$keyword$close"
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Keyword
        return other.keyword == keyword
    }

    override fun hashCode(): Int
    {
        return keyword.hashCode()
    }

    fun addChild(child: Keyword)
    {
        child.parent = this
        children.add(child)
    }

    fun interpret(context: ProgramScope): ProgramValue?
    {
        return interpretFun.invoke(InterpreterFactoryObject, this, context) as ProgramValue?
    }
}