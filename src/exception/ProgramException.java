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
		ZERO_DIVISION("Divide by zero");
		@Getter
		final String msg;
		ExceptionType(String s)
		{
			msg = s;
		}
	}
}
