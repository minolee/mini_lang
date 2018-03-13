package scanner;

import structure.Keyword;

import java.util.function.Function;

public class KeywordGenerator
{
	@SuppressWarnings("unchecked")
	public static Function<String, Keyword> GetGeneratorFunctionFromName(String functionName)
	{

		try
		{
			return ( (Function<String, Keyword>) KeywordGenerator.class.getDeclaredField(functionName).get(null) );
		}
		catch (IllegalAccessException|NoSuchFieldException e)
		{
			return null;
		}
	}

	public static Function<String, Keyword> NUM = (s -> new Keyword("NUMBER", true, Integer.parseInt(s)));
	

}
