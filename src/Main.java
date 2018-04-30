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

        Parser p = Parser.GenerateParser(new File(language_definition_dir+minilang_prefix+"grammar_test"));

		File f = null;
		System.out.println(args.length);
		if(args.length > 0)
			f = new File(args[0]);
		else
			f = new File(test_file_dir+minilang_prefix+"printTest.ml");
        Keyword root = p.parse(f, s);

        Interpreter i = Interpreter.GenerateInterpreter(root);

		i.getRoot().printAST(0);
//        i.interpret();
    }
}
