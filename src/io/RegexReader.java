package io;

import scanner.Automata;
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
    public static List<Automata> read(String fileName)
    {
        List<Automata> result = new ArrayList<>();
        try
        {
            BufferedReader r = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = r.readLine()) != null)
            {
                result.add(new Automata(line));
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
