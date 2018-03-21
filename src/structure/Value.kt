package structure

class Value {
    var floatVal: Float? = null
    var intVal: Int? = null
    val type: Type
    constructor(x: Float)
    {
        floatVal = x
        type = Type.Float
    }
    constructor(x: Int)
    {
        intVal = x
        type = Type.Int
    }
    enum class Type
    {
        Float, Int
    }
}