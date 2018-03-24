import exception.ParseException;
import exception.ScannerException;
import interpreter.Interpreter;
import parser.Parser;
import scanner.Scanner;
import structure.Keyword;

import java.io.File;
import java.io.IOException;

/**
 * Created by pathm on 2017-08-28.
 */
class Main
{
    private static final String language_definition_dir = "lang_def/";
    private static final String test_file_dir = "testfiles/";
    private static final String minilang_prefix = "minilang_";
    public static void main(String[] args) throws IOException, ScannerException, ParseException
    {
	    Scanner s = Scanner.readKeywords(language_definition_dir + minilang_prefix + "keywords");
//        for(Keyword word : s.scan(new File(test_file_dir+ minilang_prefix + "scannerTest.ml")))
//        {
//            System.out.println(word.toString());
//        }
        Parser p = Parser.GenerateParser(new File(language_definition_dir+minilang_prefix+"grammar"));

//        p.grammar.forEach((k, v) ->
//		{
//			for(ProductionRule pe : v)
//			{
//				System.out.println(pe);
//			}
//		});
        Keyword root = p.parse(new File(test_file_dir+minilang_prefix+"miniTest.ml"), s);
//        System.out.println(FunctionFinder.FindParseFunctionByName("case"));
        Interpreter i = Interpreter.GenerateInterpreter(root);
        i.interpret();
    }
}
