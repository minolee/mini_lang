package structure;

import lombok.Getter;

import java.util.List;

/**
 * Created by pathm on 2017-08-28.
 */
public class Node
{
    @Getter
    List<Node> child;
    @Getter
    Node parent;
    public Node()
    {

    }

    public Object treewalk()
    {
        return null;
    }
}
