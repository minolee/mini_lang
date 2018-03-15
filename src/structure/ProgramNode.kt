package structure

abstract class ProgramNode
{
    var parent: ProgramNode? = null
    val children = ArrayList<ProgramNode>()
    var treewalkFun: ()->Any = {}
}