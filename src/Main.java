import error.ScannerException;
import scanner.Scanner;
import structure.Keyword;

import java.io.File;
import java.io.IOException;

/**
 * Created by pathm on 2017-08-28.
 */
public class Main
{
    private static final String language_definition_dir = "lang_def/";
    private static final String test_file_dir = "testfiles/";
    public static void main(String[] args) throws IOException, ScannerException
    {
        Scanner s = Scanner.readKeywords(language_definition_dir + "keywords");
        for(Keyword word : s.scan(new File(test_file_dir+"scannerTest.ml")))
        {
            System.out.println(word.toString());
        }
    }
}
