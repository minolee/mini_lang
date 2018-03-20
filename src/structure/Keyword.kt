package structure

class Keyword
{
    constructor(keyword: String, isTerminal: Boolean)
    {
        this.keyword = keyword
        this.isTerminal = isTerminal
    }

    val keyword: String
    val isTerminal: Boolean
    var strValue: String? = null
    var intValue: Int? = null
    var floatValue: Float? = null
    var keywordType: String = "None"
    var parent: Keyword? = null
    val children = ArrayList<Keyword>()
    var treewalkFun: ()->Any = {}
    var reduceFun = util.FunctionFinder.FindParseFunctionByName("Default")

    companion object
    {
        @JvmField
        val EOF = Keyword("eof", true)
        @JvmField
        val EPSILON = Keyword("epsilon", true)
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


}