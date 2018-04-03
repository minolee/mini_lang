package exception;

import lombok.Getter;
import structure.ProgramValue;

public class ProgramException extends Exception
{
	public ProgramValue returnValue;
	public ExceptionType type;
	public ProgramException()
	{
		super();
	}
	public ProgramException(ExceptionType type)
	{
		super(type.getMsg());
		this.type = type;
	}
	public ProgramException(ExceptionType type, String additionalMsg)
	{
		super(type.getMsg() +": "+ additionalMsg);
		this.type = type;
	}
	public enum ExceptionType
	{
		ZERO_DIVISION("Divide by zero"), ABORT("Aborted"), FREE_VARIABLE("Free variable"), PARAM_NOT_MATCH("Parameter not match"), RETURN("Return statement outside function"), NO_FUNCTION("No such function");
		@Getter
		final String msg;
		ExceptionType(String s)
		{
			msg = s;
		}
	}
}
