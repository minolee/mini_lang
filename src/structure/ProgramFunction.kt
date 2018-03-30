package structure

import exception.ProgramException

class ProgramFunction(k: Keyword)
{
	val invokeTarget = k // function
	val params = HashMap<String, ProgramValue>()
	val paramNames = ArrayList<String>()
	init
	{
		//generate parameter space
		for (node in k.children)
		{
			if(node.keyword == "ID")
			{
				val name = node.strValue!!
				params.put(name, ProgramValue.DummyValue())
				paramNames.add(name)
			}
		}
	}
	operator fun invoke(vararg param: ProgramValue)
	{
		if(params.size != param.size) throw ProgramException(ProgramException.ExceptionType.PARAM_NOT_MATCH)
		for(i in 0..paramNames.size)
		{
			params[paramNames[i]] = param[i]
		}
		//함수 interpret할 때 parameter를 어떻게 가져올 것인가,
	}
}