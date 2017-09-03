package parser;

import structure.Keyword;
import structure.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-08-28.
 * parse grammar
 */
class Parser
{
    private List<ParseState> states;
    private ParseState currentState;
    private List<Keyword> legalKeywords;
    Parser()
    {
        states = new ArrayList<>();
        legalKeywords = new ArrayList<>();
    }

    private void addKeyword(Keyword k)
    {
        legalKeywords.add(k);
    }



    public static Parser generateTable(File grammarFile)
    {
        Parser p = new Parser();
        return null;
    }

    /**
     * 프로그램을 parse하여 하나의 node tree로 만든다.
     * @param file
     * @return
     */
    public Node parse(String file)
    {
        return null;
    }
}
