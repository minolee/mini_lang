package automaton;

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
class State<T> {
    static final String ANY = "ANY";
    Map<T, List<State<T>>> transition;
    private List<State<T>> epsilonMovement;
    List<State<T>> anyMovement;
    T except;
    List<State<T>> exceptMovement;
    @Getter @Setter
    boolean accepting;
    @Setter
    boolean exceptMode;

    State(boolean acceptingState)
    {
        transition = new HashMap<>();
        epsilonMovement = new ArrayList<>();
        exceptMovement = new ArrayList<>();
        anyMovement = new ArrayList<>();
        accepting = acceptingState;
    }

    List<State<T>> transit(T input)
    {
        List<State<T>> result = new ArrayList<>();
        result.addAll(epsilonMovement);
        if(transition.get(input) != null) transition.get(input).forEach(state -> {
            if(!result.contains(state)) result.add(state);
        });
        if(input != null)
        {
            anyMovement.forEach(state -> {
                if(!result.contains(state)) result.add(state);
            });

            if(exceptMode && except != input)
            {
                result.addAll(exceptMovement);
            }

        }
        return result;
    }

    void addTransition(T key, State<T> newState)
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

    void addAnyMovement(State<T> state)
    {
        anyMovement.add(state);
    }
    void setExceptInput(T exceptChar)
    {
        except = exceptChar;
        exceptMode = true;
    }
    void addExceptMovement(State<T> newState)
    {
        exceptMovement.add(newState);
    }
}
