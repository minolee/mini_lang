package structure

import interpreter.InterpretFunctionFactory
import interpreter.ScopeGeneratorFunctionFactory
import parser.ReduceFunctionFactory
import util.ClassFinder
import util.FunctionFinder
import java.lang.reflect.InvocationTargetException

open class Keyword
{

	constructor(keyword: String, isTerminal: Boolean, isVisible: Boolean)
	{
		this.keyword = keyword
		this.isTerminal = isTerminal
		this.isVisible = isVisible
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
	var parent: Keyword = this
	val children = ArrayList<Keyword>()
	var original: String? = null
	var root = this

	companion object
	{
		@JvmField
		val EOF = Keyword("eof", true)
		@JvmField
		val EPSILON = Keyword("epsilon", true)
		val InterpreterFactoryObject = InterpretFunctionFactory()
		val ScopeGenFunFactoryObject = ScopeGeneratorFunctionFactory()
		val ParseFunctionFactoryObject = ReduceFunctionFactory()
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

	fun reduce(context: List<Keyword>) = FunctionFinder.FindParseFunctionByName(keyword.toLowerCase()).invoke(ParseFunctionFactoryObject, this, context)

	fun interpret(): ProgramValue?
	{
		try
		{
			return FunctionFinder.FindInterpretFunctionByName(keyword.toLowerCase()).invoke(InterpreterFactoryObject, this) as ProgramValue?
		}
		catch(e: InvocationTargetException)
		{
			throw e.targetException
		}

	}

	fun generateScope()
	{
		FunctionFinder.FindScopeGenerationFunction(keyword.toLowerCase()).invoke(ScopeGenFunFactoryObject, this)
	}


	fun rebuild(): Keyword
	{
		val instance = ClassFinder.FindProgramStructureByName(keyword)?.getDeclaredConstructor(Keyword::class.java)?.newInstance(this)
				?: this
		instance.parent = this.parent
		val mapped = instance.children.map(Keyword::rebuild)
		instance.children.clear()
		mapped.forEach { instance.addChild(it) }
		return instance
	}

	fun addBoundVariable(x: String)
	{

		if (this !is ProgramNode.ScopeContainingKeyword)
		{
			this.parent.addBoundVariable(x)
			return
		}
		this.scope[x] = ProgramValue.DummyValue()

	}

}