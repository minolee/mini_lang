package parser

import exception.ParseException
import structure.Keyword
import java.util.*

class ParseState(initial: Closure)
{
	companion object
	{
		// actual parse function
		@JvmStatic
		fun feed(context: Stack<ParseState>, next: Keyword): Keyword?
		{

			var current = context.peek()
			if (current.currentState.shift.containsKey(next))
			{
				current.currentKeyword = next
				context.push(ParseState(current.currentState.shift[next]!!))
				return null
			}
			val candidates = ArrayList<Item>()
			current.currentState.items.forEach { if (it.next == null && it.lookahead == next) candidates.add(it) }
			if (candidates.size == 0) throw ParseException(ParseException.ExceptionType.SYNTAX_ERROR)
			context.pop()
			//top of keyword stack
			//여기부터 pop 하면서 keyword filtering 한다
			val reduceContext = ArrayList<Keyword>()
			while (candidates.size != 1)
			{
				val popstate = context.pop()
				reduceContext.add(popstate.currentKeyword!!)
				candidates.map { it.previousItem() }
				candidates.filter { popstate.currentState.items.contains(it) }
				if (context.empty()) break
			}
			if (candidates.size > 1) throw ParseException(ParseException.ExceptionType.AMBIGUOUS_GRAMMAR, "Reduce-Reduce Exception on ${candidates[0]}, ${candidates[1]}")
			//유일한 후보를 걸러냈으니 끝까지 track
			var target = candidates[0]
			while (target.previousItem() != null)
			{
				target = target.previousItem()
				current = context.pop()
				reduceContext.add(current.currentKeyword!!)
			}
			//이 시점에서 item의 맨 앞까지 pop했으므로 유도 keyword를 push하고 종료
			reduceContext.reverse()
			val push = target.generatingKeyword
			push.reduce(reduceContext)
			current.currentKeyword = push

			context.push(current)
			if (next == Keyword.EOF && push.keyword == "PROGRAM")
			{
				//set root of every keyword tree to this
				val queue = ArrayDeque<Keyword>()
				queue.push(push)
				while(queue.size > 0)
				{
					val curr = queue.pop()
					queue.addAll(curr.children)
					curr.root = push
				}
				return push
			}
			context.push(ParseState(current.currentState.shift[push]!!))
			//reduce 완료. 다음 keyword를 feed한다.
			feed(context, next)
			return null
		}
	}

	val currentState = initial
	var currentKeyword: Keyword? = null
}

