package automaton;

/**
 * Created by pathm on 2017-09-04.
 */
public class RegexElement<T>
{
    final T data;
    final char command;
    final boolean commandMode;
    public RegexElement(T data)
    {
        this.data = data;
        this.command = 0;
        this.commandMode = false;
    }
    public RegexElement(char command)
    {
        this.data = null;
        this.command = command;
        this.commandMode = true;
    }
}
