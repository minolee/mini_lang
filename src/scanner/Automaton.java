package scanner;

import lombok.NonNull;

import java.util.*;
import java.util.function.Consumer;

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
        RegexTree tree = parseRegex(line, null);
        Automaton temp = interpretRegexTree(tree);
        temp = reduce(temp);
        return temp;
    }

    private static RegexTree parseRegex(String line, RegexTree context) throws ScannerException
    {
        if(line.length() == 0) return context;
        char[] x = line.toCharArray();
        RegexTree regexTree = null;
        RegexOperation op;
        int readlen = 0;
        String specialLetters = "()[]|^*.";
        int level;
        StringBuilder temp;
        switch(x[0])
        {
            case '\\':
                char special = x[1];
                RegexTree rt = null;
                switch(special)
                {
                    case 'n':
                        rt = new RegexTree('\n');
                        break;
                    case 'r':
                        rt = new RegexTree('\r');
                        break;
                    case 't':
                        rt = new RegexTree('\t');
                        break;
                    case '\\':
                        rt = new RegexTree('\\');
                        break;
                    default:
                        rt = new RegexTree(x[1]);
                        break;
                }
                if(context != null)
                {
                    regexTree = new RegexTree(RegexOperation.CONCAT);
                    regexTree.left = context;
                    regexTree.right = rt;
                }
                else
                    regexTree = rt;
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
                rt =  new RegexTree(RegexOperation.NOT);
                String next = ""+x[1];
                readlen = 2;
                if(x[1] == '\\')
                {
                    if(x.length < 3) throw new ScannerException("No symbol after \\");
                    next += x[2];
                    readlen = 3;
                }
                rt.left = parseRegex(next, null);
                if(context != null)
                {
                    regexTree = new RegexTree(RegexOperation.CONCAT);
                    regexTree.left = context;
                    regexTree.right = rt;
                }
                else
                    regexTree = rt;
                break;
            case '*':
                if(context == null) throw new ScannerException("no preceding regex front of *");
                RegexTree r = new RegexTree(RegexOperation.REPEAT);
                if(context.op == RegexOperation.NONE || context.op == RegexOperation.ALL)
                {
                    r.left = context;
                    regexTree = r;
                }
                else
                {
                    r.left = context.right;
                    context.right = r;
                    regexTree = context;
                }
                readlen = 1;
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
                if(context == null)
                {
                    regexTree = new RegexTree(temp.substring(1, k - 1));
                }
                else
                {
                    regexTree = new RegexTree(RegexOperation.CONCAT);
                    regexTree.left = context;
                    regexTree.right = new RegexTree(temp.substring(1, k - 1));
                }
                readlen = k;
                break;
            case '.':
                if(context == null)
                {
                    regexTree = new RegexTree(RegexOperation.ALL);
                }
                else
                {
                    regexTree = new RegexTree(RegexOperation.CONCAT);
                    regexTree.left = context;
                    regexTree.right = new RegexTree(RegexOperation.ALL);
                }
                readlen = 1;
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

        return parseRegex(line.substring(readlen), regexTree);
    }

    public static String parseRange(String range) throws ScannerException
    {
        StringBuilder result = new StringBuilder();
        char[] x = range.toCharArray();
        for (int i = 0; i < x.length; i++)
        {
            char next = x[i];
            if(next != '-') result.append(next);
            else
            {
                char from = result.charAt(result.length() - 1);
                char to = x[++i];
                if(from > to) throw new ScannerException("range not match");
                for(char c = ((char) (from + 1));c <= to;c++)
                {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    private static Automaton interpretRegexTree(RegexTree tree) throws ScannerException
    {
        Automaton result = new Automaton();
        State start = new State(false);
        State end = new State(true);
        result.addState(start, true);
        result.addState(end);
        Automaton left, right;
        Consumer<State> merge = src ->
        {
            src.setAccepting(false);
            src.addTransition(null, end);
        };
        switch (tree.op)
        {
            case NONE:
                start.addTransition(new String(new char[]{tree.data}), end);
                break;
            case OR:
                left = interpretRegexTree(tree.left);
                right = interpretRegexTree(tree.right);
                start.addTransition(null, left.startState);
                start.addTransition(null, right.startState);
                left.getEndStates().forEach(merge);
                right.getEndStates().forEach(merge);
                left.states.forEach(result::addState);
                right.states.forEach(result::addState);
                break;
            case REPEAT:
                left = interpretRegexTree(tree.left);
                start.addTransition(null, left.startState);
                left.getEndStates().forEach(merge);
                start.addTransition(null, end);
                end.addTransition(null, start);
                left.states.forEach(result::addState);
                break;
            case PLAIN:
                String range = parseRange(tree.range);
                for(char x : range.toCharArray())
                {
                    start.addTransition(new String(new char[]{x}), end);
                }
                break;
            case CONCAT:
                left = interpretRegexTree(tree.left);
                right = interpretRegexTree(tree.right);
                left.getEndStates().forEach(state ->
                {
                    state.setAccepting(false);
                    state.addTransition(null, right.startState);
                });
                right.getEndStates().forEach(merge);
                left.states.forEach(result::addState);
                right.states.forEach(result::addState);
                start.addTransition(null, left.startState);
                break;
            case ALL:
                start.addTransition(State.ANY, end);
                break;
        }
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
