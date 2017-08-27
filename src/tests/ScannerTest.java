package tests;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;
import scanner.Automaton;
import scanner.ScannerException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pathm on 2017-08-26.
 */
public class ScannerTest
{
    @Test
    public void simpleTest()
    {
        try
        {
            Automaton a = Automaton.parseLine("simple");
            a = Automaton.reduce(a);
            assertTrue(a.accepts("simple"));
            assertFalse(a.accepts("lol"));
            assertFalse(a.accepts("simplest"));
        }
        catch (ScannerException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void kleeneStarTest()
    {
        try
        {
            Automaton a = Automaton.parseLine("a*b");
            a = Automaton.reduce(a);
            assertTrue(a.accepts("ab"));
            assertTrue(a.accepts("aaaaaaaaaaaaaaaaaaaaaab"));
            assertTrue(a.accepts("b"));
            assertFalse(a.accepts("ba"));
            assertFalse(a.accepts(""));
        }
        catch(ScannerException e)
        {
            e.printStackTrace();
        }
    }

    public void bracketTest()
    {

    }

}
