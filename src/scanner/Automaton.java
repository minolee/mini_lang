package scanner;

import lombok.NonNull;

import java.util.*;

/**
 * Created by pathm on 2017-08-26.
 * 직접적으로 dfa를 만들지 말고, 반드시 파일을 읽어서 구현할 것
 */
public class Automaton
{
    private List<State> states;
    private State startState;

    private Automaton()
    {
        states = new ArrayList<>();
    }

    public static Automaton parseLine(String line) throws ScannerException
    {
        if(line.length() == 0)
        {
            Automaton result = new Automaton();
            State s = new State(true);
            result.addState(s, true);
            return result;
        }
        RegexTree parsed = parseRegex(line, null);

        Stack<Automaton> stack = new Stack<>();

        for(char next : line.toCharArray())
        {

            Automaton temp = new Automaton();
            State start = new State(false);
            State end = new State(true);
            temp.addState(start, true);
            temp.addState(end);
            switch(next)
            {
                case '^':

                    break;
                case '*':
                    Automaton last = stack.pop();
                    for(State s : last.getEndStates())
                    {
                        s.addTransition(null, end);
                        s.setAccepting(false);
                    }
                    start.addTransition(null, end);
                    start.addTransition(null, last.startState);
                    end.addTransition(null, start);
                    for(State s : last.states)
                    {
                        temp.addState(s);
                    }
                    break;
                default:
                    start.addTransition(new String(new char[]{next}), end);
                    break;
            }
            stack.push(temp);
        }

        Automaton result = stack.pop();
        while(!stack.empty())
        {
            Automaton temp = stack.pop();
            for(State s : temp.getEndStates())
            {
                s.addTransition(null, result.startState);
                s.setAccepting(false);
            }
            temp.states.forEach(result::addState);
            result.startState = temp.startState;
        }
        return result;

    }

    private static RegexTree parseRegex(String line, RegexTree context) throws ScannerException
    {

        char[] x = line.toCharArray();
        RegexTree regexTree = null;
        RegexOperation op;
        int readlen = 0;
        String specialLetters = "()[]|^*\\";
        int level;
        StringBuilder temp;
        switch(x[0])
        {
            case '\\':
                char special = x[1];
                switch(special)
                {
                    case 'n':
                        regexTree = new RegexTree('\n');
                        break;
                    case 'r':
                        regexTree = new RegexTree('\r');
                        break;
                    case 't':
                        regexTree = new RegexTree('\t');
                        break;
                    case '\\':
                        regexTree = new RegexTree('\\');
                        break;
                    default:
                        regexTree = new RegexTree(x[1]);
                        break;
                }
                readlen = 2;
                break;
            case '(':
                level = 0;
                temp = new StringBuilder();
                int j = 0;
                do
                {
                    temp.append(x[j]);
                    if(x[j] == '(' && (j == 0 || x[j - 1] != '\\')) level++;
                    if(x[j] == ')' && (j == 0 || x[j - 1] != '\\')) level--;
                    j++;
                }while(level > 0 && j < x.length);
                if(level > 0) throw new ScannerException("()");
                regexTree = parseRegex(temp.substring(1, j - 1), null);
                readlen = j;
                break;
            case '|':
                if(context == null) throw new ScannerException("|");
                regexTree = new RegexTree(RegexOperation.OR);
                regexTree.left = context;
                regexTree.right = parseRegex(line.substring(1), null);
                return regexTree;
            case '^':
                if(specialLetters.contains(new String(new char[]{x[1]}))) throw new ScannerException("special letter after '^'");
                regexTree = new RegexTree(RegexOperation.NOT);
                regexTree.left = new RegexTree(x[1]);
                readlen = 2;
            case '*':
                if(context == null) throw new ScannerException("no preceding regex front of *");
                regexTree = new RegexTree(RegexOperation.REPEAT);
                regexTree.left = context;
                break;
            case '[':
                level = 0;
                temp = new StringBuilder();
                int k = 0;
                do
                {
                    temp.append(x[k]);
                    if(x[k] == '[' && (k == 0 || x[k - 1] != '\\')) level++;
                    if(x[k] == ']' && (k == 0 || x[k - 1] != '\\')) level--;
                    k++;
                }while(level > 0 && k < x.length);
                if(level > 0) throw new ScannerException("[]");
                regexTree = new RegexTree(temp.substring(1, k - 1));
                readlen = k;
                break;
            default:
                if(context != null)
                {
                    regexTree = new RegexTree(RegexOperation.CONCAT);
                    regexTree.left = context;
                    regexTree.right = new RegexTree(x[0]);
                }
                else
                {
                    regexTree = new RegexTree(x[0]);
                }
                readlen = 1;
                break;
        }

        RegexTree result = new RegexTree(RegexOperation.CONCAT);
        RegexTree remaining = parseRegex(line.substring(readlen), regexTree);
        result.left = regexTree;
        result.right = parseRegex(line.substring(readlen), result);

        return result;
    }

    private void addState(State s, boolean isStartState)
    {
        states.add(s);
        if(isStartState) startState = s;
    }

    private void addState(State s)
    {
        addState(s, false);
    }

    private List<State> getEndStates()
    {
        List<State> result = new ArrayList<>();
        for(State s : states)
        {
            if(s.isAccepting())
                result.add(s);
        }
        return result;
    }

    private static Automaton removeEpsilonMovement(Automaton input)
    {

        //initialize
        Map<State, EpsilonClosure> allEpsilonClosures = new HashMap<>();
        for(State s: input.states)
        {
            allEpsilonClosures.put(s, new EpsilonClosure(getEpsilonClosure(s)));
        }

        Automaton result = new Automaton();

        Queue<State> nexts = new LinkedList<>();
        nexts.add(input.startState);
        State next;
        do{
            next = nexts.poll();
            EpsilonClosure newClosure = allEpsilonClosures.get(next);
            if(result.states.contains(newClosure)) continue;
            //merge transition
            List<String> legalCharacters = new ArrayList<>();
            for(State s : newClosure.states)
            {
                for(String in : s.transition.keySet())
                {
                    if(legalCharacters.contains(in)) continue;
                    legalCharacters.add(in);
                }
            }
            for(State s : newClosure.states)
            {
                for(String in : legalCharacters)
                {
                    if(s.transition.get(in) == null) continue;
                    for(State dest : s.transition.get(in))
                    {
                        newClosure.addTransition(in, allEpsilonClosures.get(dest));
                        nexts.add(dest);
                    }
                }
            }
            //add closure to new automaton
            result.states.add(newClosure);
        } while(nexts.size() > 0);
        result.startState = allEpsilonClosures.get(input.startState);
        return result;
    }

    private static List<State> getEpsilonClosure(State s)
    {
        List<State> closure = new ArrayList<>();
        Queue<State> nextStep = new LinkedList<>();
        nextStep.add(s);
        while(nextStep.size() > 0)
        {
            State next = nextStep.poll();
            if(closure.contains(next)) continue;
            closure.add(next);
            nextStep.addAll(next.transit(null));
        }
        return closure;
    }

    private static Automaton removeNondeterministicTransition(Automaton input) throws ReduceException
    {
        for(State s : input.states)
        {
            if(s.transit(null).size() > 0) throw new ReduceException("Input must not have any epsilon movement!");
        }
        return null;
    }

    public static Automaton reduce(Automaton input)
    {
        return removeEpsilonMovement(input);
    }


    public boolean accepts(String text)
    {
        List<State> currentState = new ArrayList<>();
        currentState.add(startState);
        List<State> nextState = new ArrayList<>();
        for(char c :text.toCharArray())
        {
            for(State current : currentState)
            {
                nextState.addAll(current.transit(new String(new char[]{c})));
            }
            currentState = nextState;
            nextState = new ArrayList<>();
        }
        for(State s : currentState)
        {
            if(s.isAccepting()) return true;
        }
        return false;
    }

    public static void main(String[] args)
    {
        try
        {
            parseLine("simple");
        }
        catch (ScannerException e)
        {
            e.printStackTrace();
        }
    }
}
