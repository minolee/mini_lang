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
    public RegexElement(char command, boolean flag) //T가 char면 이리로 올 때 문제가 생겨서 더미 플래그 넣음
    {
        this.data = null;
        this.command = command;
        this.commandMode = true;
    }
}
