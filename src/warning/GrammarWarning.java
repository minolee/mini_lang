package warning;

/**
 * Created by pathm on 2017-09-07.
 */
public class GrammarWarning
{
    public static enum Type implements WarningType
    {
        ;
        final String msg;
        Type(String msg)
        {
            this.msg = msg;
        }
        @Override
        public String defaultMsg()
        {
            return msg;
        }
    }
}
