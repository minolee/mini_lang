package structure

class ProgramValue {
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

    override fun toString(): String = when(type)
    {
        Type.Float -> floatVal.toString()
        Type.Int -> intVal.toString()
    }
}