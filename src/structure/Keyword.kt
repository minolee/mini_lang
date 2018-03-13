package structure

class Keyword constructor(val keyword: String, val isTerminal: Boolean)
{
    var strValue: String? = null
    var intValue: Int? = null
    var floatValue: Float? = null
    var keywordType: String = "None"
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

}