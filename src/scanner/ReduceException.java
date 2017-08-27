package scanner;

/**
 * Created by pathm on 2017-08-26.
 */
public class ReduceException extends Exception
{
    public ReduceException(String msg)
    {
        super("Reducing Automaton exception : " + msg);
    }
}
