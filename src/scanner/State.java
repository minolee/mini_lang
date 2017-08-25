package scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pathm on 2017-08-26.
 * DFA의 개별 state
 */
public class State {
    private Map<String, List<State>> transition;
    private List<State> epsilonMovement;
    private boolean isAccepting;

    public State(boolean acceptingState)
    {
        transition = new HashMap<>();
        epsilonMovement = new ArrayList<>();
        isAccepting = acceptingState;
    }

    public List<State> transit(String input)
    {
        return transition.get(input);
    }

    public void addTransition(String key, State newState)
    {
        transition.putIfAbsent(key, new ArrayList<>());
        if(!transition.get(key).contains(newState))
            transition.get(key).add(newState);
    }

    public boolean isAccepting()
    {
        return isAccepting;
    }
}
