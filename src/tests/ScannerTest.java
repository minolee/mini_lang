package tests;
import org.junit.Test;
import scanner.Automaton;
import scanner.ScannerException;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pathm on 2017-08-26.
 * test cases for Regex parser
 */
public class ScannerTest
{

    @Test
    public void parseRangeTest()
    {
        try
        {
            String[] testSet = new String[4];
            String[] answerSet = new String[4];
            testSet[0] = "abcde";
            testSet[1] = "a-z";
            testSet[2] = "a-zA";
            testSet[3] = "a-zA-Z0-9";

            answerSet[0] = "abcde";

            StringBuilder temp = new StringBuilder();

            for(char x = 'a';x <= 'z';x++)
            {
                temp.append(x);
            }
            answerSet[1] = temp.toString();

            temp = new StringBuilder();
            for(char x = 'a';x <= 'z';x++)
            {
                temp.append(x);
            }
            temp.append('A');
            answerSet[2] = temp.toString();

            temp = new StringBuilder();
            for(char x = 'a';x <= 'z';x++)
            {
                temp.append(x);
            }
            for(char x = 'A';x <= 'Z';x++)
            {
                temp.append(x);
            }
            for(char x = '0';x <= '9';x++)
            {
                temp.append(x);
            }
            answerSet[3] = temp.toString();

            for (int i = 0; i < 4; i++)
            {
                String result = Automaton.parseRange(testSet[i]);
                for(char x : answerSet[i].toCharArray())
                {
                    assertTrue(String.format("%s must contain %c", testSet[i], x), result.contains(new String(new char[]{x})));
                }
            }

        }
        catch(ScannerException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void simpleTest()
    {
        try
        {
            Automaton a = Automaton.parseLine("simple");
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
            assertTrue(a.accepts("ab"));
            assertTrue(a.accepts("aaaaaaaaaaaaaaaaaaaaaab"));
            assertTrue(a.accepts("b"));
            assertFalse(a.accepts("ba"));
            assertFalse(a.accepts(""));

            Automaton b = Automaton.parseLine("A*B*");
            assertTrue(b.accepts(""));
            assertTrue(b.accepts("AAAAAAAAAABBBBBBBBBBBBBBB"));
            assertTrue(b.accepts("A"));
            assertTrue(b.accepts("B"));
            assertFalse(b.accepts("BA"));
            assertFalse(b.accepts("AAAAAAAAAAAAABBBBA"));
        }
        catch(ScannerException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void orTest() throws ScannerException
    {
        Automaton a = Automaton.parseLine("a|b");
        assertTrue(a.accepts("a"));
        assertTrue(a.accepts("b"));
        assertFalse(a.accepts("ab"));

        Automaton b = Automaton.parseLine("a|b*");
        assertTrue(b.accepts("a"));
        assertTrue(b.accepts("bbbbbbbbbbbbb"));
        assertTrue(b.accepts(""));
        assertFalse(b.accepts("abbbb"));
    }

    @Test
    public void bracketTest() throws ScannerException
    {
        Automaton a = Automaton.parseLine("a[a-z]");
        String[] testSet = new String[26];
        for (int i = 0; i < 26; i++)
        {
            String test = String.format("a%c", ((char) ('a' + i)));
            testSet[i] = test;
        }

        for(String s : testSet)
        {
            assertTrue(a.accepts(s));
        }
        assertFalse(a.accepts("ff"));
        assertFalse(a.accepts("a"));
    }

    @Test
    public void specialCharacterTest() throws ScannerException
    {
        Automaton a = Automaton.parseLine("\\|\\|");
        assertTrue(a.accepts("||"));
        assertFalse(a.accepts("|"));
        assertFalse(a.accepts("\\|\\|"));

        Automaton b = Automaton.parseLine("\\|\\||or");
        assertTrue(b.accepts("||"));
        assertTrue(b.accepts("or"));
        assertFalse(b.accepts("\\|\\|"));

        Automaton c = Automaton.parseLine("\\n");
        assertTrue(c.accepts("\n"));
        assertFalse(c.accepts("\\n"));
    }

    @Test
    public void allTest() throws ScannerException
    {
        Automaton a = Automaton.parseLine(".");
        assertTrue(a.accepts("a"));
        assertTrue(a.accepts("8"));
        assertTrue(a.accepts("."));
        assertFalse(a.accepts("123"));

        Automaton b = Automaton.parseLine(".*\\n");
        assertTrue(b.accepts("asfuioaw n ruiowaenhruiwhr 9wah4rw3894hjr3wenrfjsdahnfuioahsroew\n"));
        assertTrue(b.accepts("sajdfkwaejr 0ojw3\najskdlj\\dj28j8\n"));
    }

    @Test
    public void complexTest() throws ScannerException
    {
        Automaton a = Automaton.parseLine("[a-zA-Z_][a-zA-Z0-9_]*");
        assertTrue(a.accepts("id_is_not_"));
        assertFalse(a.accepts("(id_must_not_contain_special_character)"));
        assertFalse(a.accepts("1_id_start_with_num"));
        assertFalse(a.accepts("id containing spaces"));

        Automaton b = Automaton.parseLine("//.*\\n|/\\*.*\\*/| |\\n|\\r\\n|\\t");
        assertTrue(b.accepts("//we need some comments here. so this test is important\n"));
        assertTrue(b.accepts("/*inline comments are sometimes useful*/"));
        assertTrue(b.accepts("/*multi-line\ncomments\n*/"));
        assertTrue(b.accepts(" "));
        assertTrue(b.accepts("\n"));
        assertFalse(b.accepts("//illegal comment without newline"));

        Automaton c = Automaton.parseLine("(0|1|45)*[2-3]*abc");
        assertTrue(c.accepts("000011101010101101010110454501001450103abc"));
        assertTrue(c.accepts("4545454545454545abc"));
    }

    @Test(expected= ScannerException.class)
    public void illegalRegexTest() throws Exception, ScannerException
    {
        String[] testSet = new String[]{"())", "][[]", "*a", "(", "|()()()", "a|*"};
        for(String s : testSet)
        {
            try
            {
                Automaton a = Automaton.parseLine(s);
                throw new Exception();
            }
            catch(ScannerException e)
            {

            }
        }
        throw new ScannerException();
    }

}
