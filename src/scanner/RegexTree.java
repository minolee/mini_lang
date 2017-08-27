package scanner;

/**
 * Created by pathm on 2017-08-27.
 */
class RegexTree
{
    final RegexOperation op;
    char data;
    RegexTree left;
    RegexTree right;
    String range;
    RegexTree(RegexOperation op)
    {
        this.op = op;
    }
    RegexTree(char data)
    {
        this.op = RegexOperation.NONE;
        this.data = data;
    }
    RegexTree(String data)
    {
        this.range = data;
        this.op = RegexOperation.PLAIN;
    }
}

enum RegexOperation
{
    NONE, OR, NOT, REPEAT, PLAIN, CONCAT
}