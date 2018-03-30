package util;

import structure.Keyword;

public class ClassFinder
{
	@SuppressWarnings("unchecked")
	public static Class<? extends Keyword> FindProgramStructureByName(String structureName)
	{
		try
		{
			return (Class<Keyword>) Class.forName("structure.ProgramNode$"+structureName.toLowerCase());
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}
}
