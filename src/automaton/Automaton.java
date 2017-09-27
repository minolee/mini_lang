package automaton;

import error.ScannerException;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by pathm on 2017-08-26.
 * 직접적으로 dfa를 만들지 말고, 반드시 파일을 읽어서 구현할 것
 */
public class Automaton<T>
{
    private List<State<T>> states;
    private State<T> startState;
    List<State<T>> currentState;
    @Getter@Setter
    private String name;
    private Automaton()
    {
        states = new ArrayList<>();
        currentState = new ArrayList<>();
    }

    public void initialize()
    {
        currentState.clear();
        currentState.add(startState);
    }

    public boolean accepts(Collection<T> text)
    {
        currentState.clear();
        currentState.add(startState);
        List<State<T>> nextState = new ArrayList<>();
        for(T c :text)
        {
            for(State<T> current : currentState)
            {
                nextState.addAll(current.transit(c));
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

    public boolean acceptsNext(T next, boolean transit)
    {
        List<State<T>> nextState = new ArrayList<>();
        for(State<T> s : currentState)
        {
            nextState.addAll(s.transit(next));
        }
        if(transit)
        {
            currentState = nextState;
            return currentState.size() > 0;
        }
        return nextState.size() > 0;
    }

    public boolean acceptsCurrent()
    {
        for(State s : currentState)
        {
            if(s.isAccepting()) return true;
        }
        return false;
    }

    public static <T>Automaton<T> parseLine(String line, Function<String, List<RegexElement<T>>> preprocess) throws ScannerException
    {
        RegexTree<T> tree = parseRegex(preprocess.apply(line), null);
        Automaton<T> temp = interpretRegexTree(tree);
        temp = reduce(temp);
        return temp;
    }

    public static <T> RegexTree<T> parseRegex(List<RegexElement<T>> line, RegexTree<T> context) throws ScannerException
    {
        if(line.size() == 0) return context;
        RegexTree<T> regexTree = null;
        int readlen = 0;
        int level;
        List<RegexElement<T>> temp;
        if(line.get(0).commandMode)
        {
            switch (line.get(0).command)
            {
//                case '\\':
//                    char special = x[1];
//                    RegexTree<T> rt = null;
//                    switch (special)
//                    {
//                        case 'n':
//                            rt = new RegexTree<>('\n');
//                            break;
//                        case 'r':
//                            rt = new RegexTree<>('\r');
//                            break;
//                        case 't':
//                            rt = new RegexTree<>('\t');
//                            break;
//                        case '\\':
//                            rt = new RegexTree<>('\\');
//                            break;
//                        default:
//                            rt = new RegexTree(x[1]);
//                            break;
//                    }
//                    if (context != null)
//                    {
//                        regexTree = new RegexTree<>(RegexOperation.CONCAT);
//                        regexTree.left = context;
//                        regexTree.right = rt;
//                    } else
//                        regexTree = rt;
//                    readlen = 2;
//                    break;
                case '(':
                    level = 0;
                    temp = new ArrayList<>();
                    int j = 0;
                    do
                    {
                        temp.add(line.get(j));
                        if (line.get(j).command == '(' && (j == 0 || line.get(j - 1).command != '\\')) level++;
                        if (line.get(j).command == ')' && (j == 0 || line.get(j - 1).command != '\\')) level--;
                        j++;
                    } while (level > 0 && j < line.size());
                    if (level > 0) throw new ScannerException(ScannerException.ExceptionType.NO_MATCHING_PAIR, "()");


                    if (context != null)
                    {
                        regexTree = new RegexTree<>(RegexOperation.CONCAT);
                        regexTree.left = context;
                        regexTree.right = parseRegex(temp.subList(1, j - 1), null);
                    }
                    else
                    {
                        regexTree = new RegexTree<>(RegexOperation.DUMMY);
                        regexTree.left = parseRegex(temp.subList(1, j - 1), null);
                    }
                    readlen = j;
                    break;
                case '|':
                    if (context == null) throw new ScannerException(ScannerException.ExceptionType.NO_PRECEDENCE, "|");
                    regexTree = new RegexTree<>(RegexOperation.OR);
                    regexTree.left = context;
                    regexTree.right = parseRegex(line.subList(1, line.size()), null);
                    return regexTree;
                case '*':
                    if (context == null) throw new ScannerException(ScannerException.ExceptionType.NO_PRECEDENCE, "*");
                    RegexTree<T> r = new RegexTree<>(RegexOperation.REPEAT);
                    if (context.op != RegexOperation.CONCAT)
                    {
                        r.left = context;
                        regexTree = r;
                    } else
                    {
                        r.left = context.right;
                        context.right = r;
                        regexTree = context;
                    }
                    readlen = 1;
                    break;
                case ')':
                    throw new ScannerException(ScannerException.ExceptionType.NO_MATCHING_PAIR, new String(new char[]{line.get(0).command}));
                case '.':
                    if (context == null)
                    {
                        regexTree = new RegexTree<>(RegexOperation.ALL);
                    } else
                    {
                        regexTree = new RegexTree<>(RegexOperation.CONCAT);
                        regexTree.left = context;
                        regexTree.right = new RegexTree<>(RegexOperation.ALL);
                    }
                    readlen = 1;
                    break;
                case '?':
                    if (context == null) throw new ScannerException(ScannerException.ExceptionType.NO_PRECEDENCE, "?");
                    r = new RegexTree<>(RegexOperation.ONCE);
                    if (context.op != RegexOperation.CONCAT)
                    {
                        r.left = context;
                        regexTree = r;
                    } else
                    {
                        r.left = context.right;
                        context.right = r;
                        regexTree = context;
                    }
                    readlen = 1;
                    break;
                case '^':
                    r = parseRegex(line.subList(1, 2), null);
                    regexTree = new RegexTree<>(RegexOperation.EXCEPT);
                    regexTree.left = r;
                    readlen = 2;
                    break;
                default:

                    break;
            }
        }
        else
        {
            if (context != null)
            {
                regexTree = new RegexTree<>(RegexOperation.CONCAT);
                regexTree.left = context;
                regexTree.right = new RegexTree<>(line.get(0).data);
            } else
            {
                regexTree = new RegexTree<>(line.get(0).data);
            }
            readlen = 1;
        }

        return parseRegex(line.subList(readlen, line.size()), regexTree);
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
                if(from > to) throw new ScannerException(ScannerException.ExceptionType.INVALID_RANGE, "");
                for(char c = ((char) (from + 1));c <= to;c++)
                {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    private static <T> Automaton<T> interpretRegexTree(RegexTree<T> tree) throws ScannerException
    {
        Automaton<T> result = new Automaton<>();
        State<T> start = new State<>(false);
        State<T> end = new State<>(true);
        result.addState(start, true);
        result.addState(end);
        Automaton<T> left, right;
        Consumer<State<T>> merge = src ->
        {
            src.setAccepting(false);
            src.addTransition(null, end);
        };
        switch (tree.op)
        {
            case NONE:
                start.addTransition(tree.data, end);
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
                start.addAnyMovement(end);
                break;
            case ONCE:
                left = interpretRegexTree(tree.left);
                start.addTransition(null, left.startState);
                left.getEndStates().forEach(merge);
                start.addTransition(null, end);
                left.states.forEach(result::addState);
                break;
            case DUMMY:
                result = interpretRegexTree(tree.left);
                break;
            case EXCEPT:
                T exceptchar = tree.left.data;
                start.setExceptInput(exceptchar);
                start.addExceptMovement(end);
                break;
        }

        return result;
    }

    private void addState(State<T> s, boolean isStartState)
    {
        states.add(s);
        if(isStartState) startState = s;
    }

    private void addState(State<T> s)
    {
        addState(s, false);
    }

    private List<State<T>> getEndStates()
    {
        List<State<T>> result = new ArrayList<>();
        for(State<T> s : states)
        {
            if(s.isAccepting())
                result.add(s);
        }
        return result;
    }

    private static <T>Automaton<T> removeEpsilonMovement(Automaton<T> input)
    {

        //initialize
        final Map<State<T>, EpsilonClosure<T>> allEpsilonClosures = new HashMap<>();
        for(State<T> s: input.states)
        {
            allEpsilonClosures.put(s, new EpsilonClosure<>(getEpsilonClosure(s)));
        }

        Automaton<T> result = new Automaton<>();

        Queue<State<T>> nexts = new LinkedList<>();
        nexts.add(input.startState);
        State<T> next;
        do{
            next = nexts.poll();
            EpsilonClosure<T> newClosure = allEpsilonClosures.get(next);
            if(result.states.contains(newClosure)) continue;
            //merge transition
            List<T> legalCharacters = new ArrayList<>();
            for(State<T> s : newClosure.states)
            {
                for(T in : s.transition.keySet())
                {
                    if(legalCharacters.contains(in)) continue;
                    legalCharacters.add(in);
                }
            }
            for(State<T> s : newClosure.states)
            {
                for(T in : legalCharacters)
                {
                    if(s.transition.get(in) == null) continue;
                    for(State<T> dest : s.transition.get(in))
                    {
                        newClosure.addTransition(in, allEpsilonClosures.get(dest));
                        nexts.add(dest);
                    }
                }
                s.anyMovement.forEach(state -> {
                    newClosure.addAnyMovement(allEpsilonClosures.get(state));
                    nexts.add(state);
                });

                if(s.exceptMode)
                {
                    newClosure.setExceptInput(s.except);
                    s.exceptMovement.forEach(state -> {
                        newClosure.addExceptMovement(allEpsilonClosures.get(state));
                        nexts.add(state);
                    });
                }
            }
            //add closure to new automaton
            result.states.add(newClosure);
        } while(nexts.size() > 0);
        result.startState = allEpsilonClosures.get(input.startState);
        return result;
    }

    private static <T> List<State<T>> getEpsilonClosure(State<T> s)
    {
        List<State<T>> closure = new ArrayList<>();
        Queue<State<T>> nextStep = new LinkedList<>();
        nextStep.add(s);
        while(nextStep.size() > 0)
        {
            State<T> next = nextStep.poll();
            if(closure.contains(next)) continue;
            closure.add(next);
            nextStep.addAll(next.transit(null));
        }
        return closure;
    }


    public static <T>Automaton<T> reduce(Automaton<T> input)
    {
        return removeEpsilonMovement(input);
    }




}
