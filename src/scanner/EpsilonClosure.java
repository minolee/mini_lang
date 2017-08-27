package scanner;

import java.util.List;

/**
 * Created by pathm on 2017-08-27.
 */
class EpsilonClosure extends State
{
    List<State> states;

    EpsilonClosure(List<State> states)
    {
        super(false);
        for(State s: states)
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
