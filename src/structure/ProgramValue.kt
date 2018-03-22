package structure

import exception.ProgramException

//일단 string은 없는 타입이라고 하자
class ProgramValue<out T : Number>(x: T)
{
    val value = x

    override fun toString(): String = value.toString()

    operator fun plus(other: ProgramValue<*>): ProgramValue<*>
    {
        val result: ProgramValue<*>
        if (other.value is Int && this.value is Int)
        {
            val a = other.value
            val b = this.value
            result = ProgramValue(a + b)
        }
        else
        {
            val a = other.value as Float
            val b = this.value as Float
            result = ProgramValue(a + b)
        }
        return result
    }

    operator fun minus(other: ProgramValue<*>) = this + (-other)
    operator fun unaryMinus(): ProgramValue<*> = when (this.value is Int)
    {
        true -> ProgramValue(-(this.value as Int))
        false -> ProgramValue(-(this.value as Float))
    }

    operator fun times(other: ProgramValue<*>) = when(this.value is Int && other.value is Int)
    {
        true -> ProgramValue((this.value as Int) * (other.value as Int))
        false -> ProgramValue((this.value as Float) * (other.value as Float))
    }

    operator fun div(other: ProgramValue<*>): ProgramValue<*>
    {
        if(other.value == 0) throw ProgramException(ProgramException.ExceptionType.ZERO_DIVISION)
        when(this.value is Int && other.value is Int)
        {
            true -> return ProgramValue((this.value as Int) / (other.value as Int))
            false -> return ProgramValue((this.value as Float) / (other.value as Float))
        }
    }

    operator fun rem(other: ProgramValue<*>): ProgramValue<*>
    {
        if(other.value == 0) throw ProgramException(ProgramException.ExceptionType.ZERO_DIVISION)
        when(this.value is Int && other.value is Int)
        {
            true -> return ProgramValue((this.value as Int) % (other.value as Int))
            false -> return ProgramValue((this.value as Float) % (other.value as Float))
        }
    }

    override fun equals(other: Any?): Boolean
    {
        if(other == null) return false
        if (other !is ProgramValue<*>) return false
        return this.value == other.value
    }

    operator fun compareTo(other: ProgramValue<*>) = when((this.value as Float) > (other.value as Float))
    {
        true -> 1
        false -> 0
    }

    override fun hashCode(): Int
    {
        return value.hashCode()
    }

}