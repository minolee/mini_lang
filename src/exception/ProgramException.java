package exception;

import lombok.Getter;

public class ProgramException extends Exception
{
	public ProgramException()
	{
		super();
	}
	public ProgramException(ExceptionType type)
	{
		super(type.getMsg());
	}
	public ProgramException(ExceptionType type, String additionalMsg)
	{
		super(type.getMsg() +": "+ additionalMsg);
	}
	public enum ExceptionType
	{
		ZERO_DIVISION("Divide by zero"), ABORT("Aborted"), ADD_FUNCTION("Trying to add function"), PARAM_NOT_MATCH("Parameter not match");
		@Getter
		final String msg;
		ExceptionType(String s)
		{
			msg = s;
		}
	}
}
