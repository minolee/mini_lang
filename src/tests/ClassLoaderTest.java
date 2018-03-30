package tests;

import org.junit.Test;
import structure.Keyword;
import structure.ProgramNode;
import util.ClassFinder;

import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.assertTrue;

public class ClassLoaderTest
{
	@Test
	public void classLoadTest()
	{
		System.out.println(ProgramNode.compound_expr.class);
		Class<Keyword> keywordClass = (Class<Keyword>) ClassFinder.FindProgramStructureByName("COMPOUND_EXPR");
		try
		{
			ProgramNode.compound_expr x = (ProgramNode.compound_expr)keywordClass.getDeclaredConstructor().newInstance();
			assertTrue(x instanceof ProgramNode.compound_expr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
