import io.RegexReader;
import scanner.Automaton;

import java.util.List;

/**
 * Created by pathm on 2017-08-28.
 */
public class Main
{
    static List<Automaton> keywordRecognizer;
    static final String language_definition_dir = "lang_def/";
    public static void main(String[] args)
    {
        keywordRecognizer = RegexReader.readKeywords(language_definition_dir + "keywords");
    }
}
