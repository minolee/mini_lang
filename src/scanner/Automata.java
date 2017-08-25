package scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pathm on 2017-08-26.
 * 직접적으로 dfa를 만들지 말고, 반드시 파일을 읽어서 구현할 것
 */
public class Automata {
    private List<State> states;
    private State startState;
    private List<State> currentState;

    public Automata(String line) throws ScannerException
    {
        this();
        parseLine(line);
    }

    private Automata()
    {
        states = new ArrayList<>();
    }

    private static State parseLine(String line) throws ScannerException
    {
        State startState = new State(false);
        char[] x = line.toCharArray();
        int pos = 0;
        boolean isInsideSquareBracket = false;
        for(char next : x)
        {
            switch(next)
            {
                case '(':
                    //level에 맞는 substring을 parse한다.
                    int currentDepth = 0;
                    int nextpos = pos;
                    StringBuilder nextlevel = new StringBuilder();
                    do {
                        char read = x[nextpos++];
                        if(read == '(') currentDepth++;
                        if(read == ')') currentDepth--;
                        nextlevel.append(read);
                    } while(currentDepth > 0);
                    parseLine(nextlevel.substring(1, nextlevel.length() - 1));
                    break;
                case ')':
                    break;
                case '*':
                case '+':
                case '^':
                case '[':
                    isInsideSquareBracket = true;
                    break;
                case ']':
                    if(!isInsideSquareBracket) throw new ScannerException("Invalid regex expression");
                    isInsideSquareBracket = false;
                    break;
                case '-':

                    break;
            }
            pos++;
        }
        return startState;
    }

    private void addState(State s)
    {
        states.add(s);
    }

}
