package structure

class ProgramFunction(k: Keyword): ProgramValue()
{
	val param = HashMap<String, ProgramValue>()
	val interpretTarget = k
}