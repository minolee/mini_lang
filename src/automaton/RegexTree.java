package automaton;

/**
 * Created by pathm on 2017-08-27.
 */
class RegexTree<T>
{
    final RegexOperation op;
    T data;
    RegexTree<T> left;
    RegexTree<T> right;
    String range;
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

enum RegexOperation
{
    NONE, OR, REPEAT, RANGE, CONCAT, ALL, ONCE, DUMMY, EXCEPT
}