package util;

import parser.ParseFunctionFactory;

import java.lang.reflect.Method;
public class FunctionFinder
{
	public static Method FindParseFunctionByName(String name)
	{
		Method method;
		try
		{
			method = ParseFunctionFactory.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException e)
		{
			return null;
		}
		return method;
	}
}
