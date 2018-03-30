package util;

import interpreter.InterpretFunctionFactory;
import interpreter.ScopeGeneratorFunctionFactory;
import parser.ReduceFunctionFactory;
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
			method = ReduceFunctionFactory.class.getDeclaredMethod(name, Keyword.class, List.class);
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				return ReduceFunctionFactory.class.getDeclaredMethod("default", Keyword.class, List.class);
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
			method = InterpretFunctionFactory.class.getDeclaredMethod(name, Keyword.class);
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

	public static Method FindScopeGenerationFunction(String name)
	{
		Method method;
		try
		{
			method = ScopeGeneratorFunctionFactory.class.getDeclaredMethod(name, Keyword.class);
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				return ScopeGeneratorFunctionFactory.class.getDeclaredMethod("default", Keyword.class);
			}
			catch (NoSuchMethodException e1)
			{
				return null;
			}
		}
		return method;
	}
}
