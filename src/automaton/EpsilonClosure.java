package automaton;

import java.util.List;

/**
 * Created by pathm on 2017-08-27.
 */
class EpsilonClosure<T> extends State<T>
{
    List<State<T>> states;

    EpsilonClosure(List<State<T>> states)
    {
        super(false);
        for(State<T> s: states)
        {
            if(s.isAccepting())
            {
                this.accepting = true;
                break;
            }
        }
        this.states = states;
    }
}
