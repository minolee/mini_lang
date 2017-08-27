package scanner;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pathm on 2017-08-26.
 * DFA의 개별 state
 */
class State {
    static final String ANY = "ANY";
    Map<String, List<State>> transition;
    List<State> epsilonMovement;
    @Getter @Setter
    boolean accepting;

    State(boolean acceptingState)
    {
        transition = new HashMap<>();
        epsilonMovement = new ArrayList<>();
        accepting = acceptingState;
    }

    List<State> transit(String input)
    {
        List<State> result = new ArrayList<>();
        result.addAll(epsilonMovement);
        if(transition.get(input) != null) transition.get(input).forEach(state -> {
            if(!result.contains(state)) result.add(state);
        });
        if(input != null)
        {
            if(transition.get(ANY) != null)
            {
                transition.get(ANY).forEach(state -> {
                    if(!result.contains(state)) result.add(state);
                });
            }
        }
        return result;
    }

    void addTransition(String key, State newState)
    {
        if(key == null)
        {
            epsilonMovement.add(newState);
            return;
        }
        transition.putIfAbsent(key, new ArrayList<>());
        if(!transition.get(key).contains(newState))
            transition.get(key).add(newState);
    }
}
