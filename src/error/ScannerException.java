package error;

import lombok.Getter;

/**
 * Created by pathm on 2017-08-26.
 * Scanner 과정에서 Exception이 생기면 이 클래스로 호출하기
 */
public class ScannerException extends Throwable{
    public ScannerException()
    {
        super();
    }
    public ScannerException(ExceptionType type)
    {
        super(type.getMsg());
    }
    public ScannerException(ExceptionType type, String msg)
    {
        super(type.getMsg() + msg);
    }

    public enum ExceptionType
    {
        BLANK_IN_NAME("Keyword name should not include blank character!"), INVALID_RANGE("Range error : "), NO_MATCHING_PAIR("No matching pair of "), NO_PRECEDENCE("No precedence of "), UNKNOWN("Unknown keyword!");
        @Getter
        final String msg;
        ExceptionType(String s)
        {
            msg = s;
        }
    }
}
