package tests;
import org.junit.Test;
import automaton.Automaton;
import error.ScannerException;
import scanner.Scanner;


import java.util.stream.Collectors;

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
            Automaton<Character> a = Automaton.parseLine("simple", Scanner::transferFunction);
            assertTrue(a.accepts("simple".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertFalse(a.accepts("lol".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertFalse(a.accepts("simplest".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
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
            Automaton<Character> a = Automaton.parseLine("a*b", Scanner::transferFunction);
            assertTrue(a.accepts("ab".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertTrue(a.accepts("aaaaaaaaaaaaaaaaaaaaaab".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertTrue(a.accepts("b".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertFalse(a.accepts("ba".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertFalse(a.accepts("".chars().mapToObj(e->(char)e).collect(Collectors.toList())));

            Automaton<Character> b = Automaton.parseLine("A*B*", Scanner::transferFunction);
            assertTrue(b.accepts("".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertTrue(b.accepts("AAAAAAAAAABBBBBBBBBBBBBBB".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertTrue(b.accepts("A".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertTrue(b.accepts("B".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertFalse(b.accepts("BA".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
            assertFalse(b.accepts("AAAAAAAAAAAAABBBBA".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        }
        catch(ScannerException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void orTest() throws ScannerException
    {
        Automaton<Character> a = Automaton.parseLine("a|b", Scanner::transferFunction);
        assertTrue(a.accepts("a".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(a.accepts("b".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("ab".chars().mapToObj(e->(char)e).collect(Collectors.toList())));

        Automaton<Character> b = Automaton.parseLine("a|b*", Scanner::transferFunction);
        assertTrue(b.accepts("a".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("bbbbbbbbbbbbb".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(b.accepts("abbbb".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
    }


    @Test
    public void specialCharacterTest() throws ScannerException
    {
        Automaton<Character> a = Automaton.parseLine("\\|\\|", Scanner::transferFunction);
        assertTrue(a.accepts("||".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("|".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("\\|\\|".chars().mapToObj(e->(char)e).collect(Collectors.toList())));

        Automaton<Character> b = Automaton.parseLine("\\|\\||or", Scanner::transferFunction);
        assertTrue(b.accepts("||".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("or".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(b.accepts("\\|\\|".chars().mapToObj(e->(char)e).collect(Collectors.toList())));

        Automaton<Character> c = Automaton.parseLine("\\n", Scanner::transferFunction);
        assertTrue(c.accepts("\n".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(c.accepts("\\n".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
    }

    @Test
    public void allTest() throws ScannerException
    {
        Automaton<Character> a = Automaton.parseLine(".", Scanner::transferFunction);
        assertTrue(a.accepts("a".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(a.accepts("8".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(a.accepts(".".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("123".chars().mapToObj(e->(char)e).collect(Collectors.toList())));

        Automaton<Character> b = Automaton.parseLine(".*\\n", Scanner::transferFunction);
        assertTrue(b.accepts("asfuioaw n ruiowaenhruiwhr 9wah4rw3894hjr3wenrfjsdahnfuioahsroew\n".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("sajdfkwaejr 0ojw3\najskdlj\\dj28j8\n".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
    }

    @Test
    public void questionMarkTest() throws ScannerException
    {
        Automaton<Character> a = Automaton.parseLine("a?b", Scanner::transferFunction);
        assertTrue(a.accepts("ab".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(a.accepts("b".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("aab".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
    }

    @Test
    public void exceptTest() throws ScannerException
    {
        Automaton<Character> a = Automaton.parseLine("^ab", Scanner::transferFunction);
        assertTrue(a.accepts("bb".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(a.accepts("tb".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("ab".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
    }

    @Test
    public void complexTest() throws ScannerException
    {
        Automaton<Character> a = Automaton.parseLine("(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|_)(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|_|0|1|2|3|4|5|6|7|8|9)*", Scanner::transferFunction);
        assertTrue(a.accepts("id_is_not_".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("(id_must_not_contain_special_character)".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("1_id_start_with_num".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(a.accepts("id containing spaces".chars().mapToObj(e->(char)e).collect(Collectors.toList())));

        Automaton<Character> b = Automaton.parseLine("//(^\\n)*|/\\*.*\\*/| |\\n|\\r\\n|\\t", Scanner::transferFunction);
        assertTrue(b.accepts("//we need some comments here. so this test is important".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("/*inline comments are sometimes useful*/".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("/*multi-line\ncomments\n*/".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts(" ".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(b.accepts("\n".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(b.accepts("//illegal comment with newline\n".chars().mapToObj(e->(char)e).collect(Collectors.toList())));


        Automaton<Character> num = Automaton.parseLine("(0|1|2|3|4|5|6|7|8|9)*(\\.(0|1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*)?", Scanner::transferFunction);
        assertTrue(num.accepts("123421".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertTrue(num.accepts("123.321".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
        assertFalse(num.accepts("123.321.123".chars().mapToObj(e->(char)e).collect(Collectors.toList())));
    }

    @Test(expected= ScannerException.class)
    public void illegalRegexTest() throws Exception, ScannerException
    {
        String[] testSet = new String[]{"())", "*a", "(", "|()()()", "a|*"};
        for(String s : testSet)
        {
            try
            {
                Automaton<Character> a = Automaton.parseLine(s, Scanner::transferFunction);
                throw new Exception();
            }
            catch(ScannerException e)
            {

            }
        }
        throw new ScannerException();
    }

}
