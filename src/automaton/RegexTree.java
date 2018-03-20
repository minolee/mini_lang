package automaton;

/**
 * Created by pathm on 2017-08-27.
 */
public class RegexTree<T>
{

    public final RegexOperation op;
    public T data;
    public RegexTree<T> left;
    public RegexTree<T> right;
    private String range;
    RegexTree(RegexOperation op)
    {
        this.op = op;
    }
    RegexTree(T data)
    {
        this.op = RegexOperation.NONE;
        this.data = data;
    }
    RegexTree(String data)
    {
        this.range = data;
        this.op = RegexOperation.RANGE;
    }
}

