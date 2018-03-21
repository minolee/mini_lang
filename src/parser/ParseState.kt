package parser

import error.ParseException
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
                println("Shift $next")
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
            //유일한 후보를 걸러냈으니 끝까지 track
            var target = candidates[0]
            while (target.previousItem() != null)
            {
                target = target.previousItem()
                current = context.pop()
                reduceContext.add(current.currentKeyword!!)
            }
            //이 시점에서 item의 맨 앞까지 pop했으므로 유도 keyword를 push하고 종료
            val push = target.generatingKeyword
            (push.reduceFun.invoke(ParseFunctionFactory(), reduceContext) as List<*>).forEach { push.addChild(it as Keyword) }
            current.currentKeyword = push
            println("reduce $push")
            context.push(current)
            if (next == Keyword.EOF && push.keyword == "PROGRAM")
            {
                println("SUCCESS!")
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

