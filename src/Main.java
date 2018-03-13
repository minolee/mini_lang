import error.ParseException;
import error.ScannerException;
import scanner.Scanner;

import java.io.File;
import java.io.IOException;

/**
 * Created by pathm on 2017-08-28.
 */
public class Main
{
    private static final String language_definition_dir = "lang_def/";
    private static final String test_file_dir = "testfiles/";
    private static final String minilang_prefix = "minilang_";
    public static void main(String[] args) throws IOException, ScannerException, ParseException
    {
        Scanner s = Scanner.readKeywords(language_definition_dir + minilang_prefix + "keywords");
        for(Keyword word : s.scan(new File(test_file_dir+ minilang_prefix + "scannerTest.ml")))
        {
            System.out.println(word.toString());
        }
//        Parser p = Parser.generateParser(new File(language_definition_dir+"grammar"));
//        p.parse("asdf");
    }
}
