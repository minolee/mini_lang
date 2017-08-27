package scanner;

/**
 * Created by pathm on 2017-08-26.
 * Scanner 과정에서 Exception이 생기면 이 클래스로 호출하기
 */
public class ScannerException extends Exception{
    public ScannerException()
    {
        super();
    }

    public ScannerException(String msg)
    {
        super("Invalid regex expression : " + msg);
    }


}
