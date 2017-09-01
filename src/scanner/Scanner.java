package scanner;

import error.ScannerException;
import structure.Keyword;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-08-31.
 */
public class Scanner
{
    List<Automaton> keywords;

    Scanner()
    {
        keywords = new ArrayList<>();
    }

    public List<Keyword> scan(File file) throws IOException, ScannerException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder buf = new StringBuilder();
        List<Keyword> result = new ArrayList<>();
        List<Automaton> candidates = new ArrayList<>(keywords);
        while((line = reader.readLine()) != null)
        {
            if(line.length() == 0) continue;
            char lastchar = 0;
            candidates.forEach(Automaton::initialize);
            for(char x : line.toCharArray())
            {
                final char last = lastchar;
                if(last != 0)
                    candidates.removeIf(a -> !a.acceptsNext(last, true));
                lastchar = x;
                int count = ((int) candidates.stream().filter(a -> a.acceptsNext(x, false)).count()); //실제로 빼지는 않았지만 이번 char를 거치면 없어질 것들
                if(count == 0)
                {
                    candidates.removeIf(a -> !a.acceptsCurrent());
                    Keyword k = new Keyword(candidates.get(0).getName(), true);

                    if(!k.getKeyword().equals("SKIP")) result.add(k);
                    candidates = new ArrayList<>(keywords); //다음 loop에서 이번 char를 쓴 것으로 filter될테니 없어져도 됨
                    candidates.forEach(Automaton::initialize);
                }
            }
            //후처리 - 마지막 글자
            final char last = lastchar;
            candidates.removeIf(a -> !a.acceptsNext(last, true));
            Keyword k = new Keyword(candidates.get(0).getName(), true);
            if(!k.getKeyword().equals("SKIP")) result.add(k);
            candidates = new ArrayList<>(keywords); //다음 loop에서 이번 char를 쓴 것으로 filter될테니 없어져도 됨
        }
        return result;
    }

    public static Scanner readKeywords(String fileName)
    {
        Scanner c = new Scanner();
        List<Automaton> result = new ArrayList<>();
        try
        {
            BufferedReader r = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = r.readLine()) != null)
            {
                System.out.println(line);
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
                System.out.println(keywordName);
                System.out.println(keywordRegex);
                Automaton a = Automaton.parseLine(keywordRegex);
                a.setName(keywordName);
                result.add(a);
            }
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
}
