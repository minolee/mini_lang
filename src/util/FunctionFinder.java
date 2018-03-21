package util;

import interpreter.InterpretFunctionFactory;
import parser.ParseFunctionFactory;
import structure.Keyword;

import java.lang.reflect.Method;
import java.util.List;

public class FunctionFinder
{
	public static Method FindParseFunctionByName(String name)
	{
		Method method;
		try
		{
			method = ParseFunctionFactory.class.getDeclaredMethod(name, List.class);
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				return ParseFunctionFactory.class.getDeclaredMethod("default", List.class);
			}
			catch (NoSuchMethodException e1)
			{
				return null;
			}
		}
		return method;
	}

	public static Method FindInterpretFunctionByName(String name)
	{
		Method method;
		try
		{
			method = InterpretFunctionFactory.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException e)
		{
            try
            {
                return InterpretFunctionFactory.class.getDeclaredMethod("default", Keyword.class);
            }
            catch (NoSuchMethodException e1)
            {
                return null;
            }
		}
		return method;
	}
}
