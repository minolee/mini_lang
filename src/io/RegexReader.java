package io;

import scanner.Automaton;
import scanner.ScannerException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-08-26.
 */
public class RegexReader {
    public static List<Automaton> readKeywords(String fileName)
    {
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
        return result;
    }
}
