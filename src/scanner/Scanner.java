package scanner;

import automaton.Automaton;
import automaton.RegexElement;
import error.ScannerException;
import structure.Keyword;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by pathm on 2017-08-31.
 */
public class Scanner
{
    List<Automaton<Character>> keywords;

    Scanner()
    {
        keywords = new ArrayList<>();
    }

    public List<Keyword> scan(File file) throws IOException
    {
        Reader reader = new BufferedReader(new FileReader(file));
        int line;
        StringBuilder buf = new StringBuilder();
        List<Keyword> result = new ArrayList<>();
        List<Automaton<Character>> candidates = new ArrayList<>(keywords);
        char lastchar = 0;
        candidates.forEach(Automaton::initialize);
        while((line = reader.read()) != -1)
        {
            final char x = (char)line;
            System.out.print(x);
            final char last = lastchar;
            buf.append(last);
            if(last != 0)
                candidates.removeIf(a -> !a.acceptsNext(last, true));
            lastchar = x;
            int count = ((int) candidates.stream().filter(a -> a.acceptsNext(x, false)).count()); //실제로 빼지는 않았지만 이번 char를 거치면 없어질 것들
            if(count == 0)
            {
                candidates.removeIf(a -> !a.acceptsCurrent());
                Automaton<Character> data = candidates.get(0);
                switch (data.getName())
                {
                    case "ID":
                    case "STRING_DATA":
                        result.add(new Keyword(data.getName(), true, buf.toString()));
                        break;
                    case "NUMBER":
                        if(buf.toString().contains("."))
                        {
                            result.add(new Keyword(data.getName(), true, Float.parseFloat(buf.toString())));
                        }
                        else
                            result.add(new Keyword(data.getName(), true, Integer.parseInt(buf.toString())));
                    case "SKIP":
                        break;
                    default:
                        result.add(new Keyword(candidates.get(0).getName(), true, null));
                }
                candidates = new ArrayList<>(keywords); //다음 loop에서 이번 char를 쓴 것으로 filter될테니 없어져도 됨
                candidates.forEach(Automaton::initialize);
                buf = new StringBuilder();
            }
        }
        //후처리 - 마지막 글자
        final char last = lastchar;
        candidates.removeIf(a -> !a.acceptsNext(last, true));
        Automaton<Character> data = candidates.get(0);
        switch (data.getName())
        {
            case "ID":
            case "STRING_DATA":
                result.add(new Keyword(data.getName(), true, buf.toString()));
                break;
            case "NUMBER":
                if(buf.toString().contains("."))
                {
                    result.add(new Keyword(data.getName(), true, Float.parseFloat(buf.toString())));
                }
                else
                    result.add(new Keyword(data.getName(), true, Integer.parseInt(buf.toString())));
            case "SKIP":
                break;
            default:
                result.add(new Keyword(candidates.get(0).getName(), true, null));
        }
        return result;
    }

    public static Scanner readKeywords(String fileName)
    {
        Scanner c = new Scanner();
        List<Automaton<Character>> result = new ArrayList<>();
        try
        {
            BufferedReader r = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = r.readLine()) != null)
            {
                String keywordName = null;
                String keywordRegex;
                if(line.startsWith("#") || line.length() == 0) continue;
                StringBuilder builder = new StringBuilder();
                int count = 0;
                for(char x : line.toCharArray())
                {
                    switch(count)
                    {
                        case 0:
                            if(x != ' ') builder.append(x);
                            else count++;
                            break;
                        case 1:
                            if(x != ':') throw new ScannerException(ScannerException.ExceptionType.BLANK_IN_NAME, "");
                            else
                            {
                                count++;
                                keywordName = builder.toString();
                                builder = new StringBuilder();
                            }
                            break;
                        case 2:
                            if(x != ' ') throw new ScannerException();
                            else count++;
                            break;
                        case 3:
                            builder.append(x);
                            break;
                    }
                }
                keywordRegex = builder.toString();
                Automaton a = Automaton.parseLine(keywordRegex, Scanner::transferFunction);
                a.setName(keywordName);
                result.add(a);
            }
            r.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println(String.format("File %s does not exist", fileName));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ScannerException e)
        {
            e.printStackTrace();
        }
        c.keywords = result;
        return c;
    }

    public static List<RegexElement<Character>> transferFunction(String line)
    {
        List<RegexElement<Character>> result = new ArrayList<>();
        char[] x = line.toCharArray();
        for (int i = 0; i < x.length; i++)
        {
            switch(x[i])
            {
                case '\\':
                    switch(x[i+1])
                    {
                        case 'n':
                            result.add(new RegexElement<>('\n'));
                            break;
                        case 'r':
                            result.add(new RegexElement<>('\r'));
                            break;
                        case 't':
                            result.add(new RegexElement<>('\r'));
                            break;
                        case '\\':
                            result.add(new RegexElement<>('\\'));
                            break;
                        default:
                            result.add(new RegexElement<>(x[i + 1]));
                            break;
                    }
                    i++;
                    break;
                case '(':
                case ')':
                case '.':
                case '^':
                case '|':
                case '*':
                case '?':
                    result.add(new RegexElement<>(x[i], true));
                    break;
                default:
                    result.add(new RegexElement<>(x[i]));
                    break;
            }
        }
        return result;
    }
}
