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

    public Automata(String line)
    {
        this();
        parseLine(line);
    }

    private Automata()
    {
        states = new ArrayList<>();
    }
    private void parseLine(String line)
    {

    }
    private void addState(State s)
    {
        states.add(s);
    }

}
