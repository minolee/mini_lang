package error;

/**
 * Created by pathm on 2017-08-28.
 */
public class ParseException extends Exception
{
    public ParseException(ExceptionType type)
    {
        super(type.msg);
    }

    public enum ExceptionType
    {
        AMBIGUOUS_GRAMMAR("Grammar is ambiguous"), ILLEGAL_GRAMMAR("Illegal grammar file");
        final String msg;
        ExceptionType(String msg)
        {
            this.msg = msg;
        }
    }
}
