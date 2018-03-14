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

    public ParseException(ExceptionType type, String additionalMsg)
    {
    	super(type.msg + ": "+additionalMsg);
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
