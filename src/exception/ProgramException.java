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

	public enum ExceptionType
	{
		ZERO_DIVISION("Divide by zero"), ABORT("Aborted"), ADD_FUNCTION("Trying to add function");
		@Getter
		final String msg;
		ExceptionType(String s)
		{
			msg = s;
		}
	}
}
