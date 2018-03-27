package structure

//program의 execution state를 저장하는 class
//scope마다 바뀌어야 함
class ProgramScope
{
	//scope의 variable list
	val scope = HashMap<String, ProgramValue>()
	var parent: ProgramScope = this
}

