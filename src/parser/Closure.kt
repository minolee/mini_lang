package parser

import structure.Keyword

class Closure internal constructor(items : Collection<Item>) {
    val items = HashSet<Item>()
    val shift = HashMap<Keyword, Closure>()
    init
    {
        this.items.addAll(items)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        items.forEach {builder.append(it)}
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if( other === this) return true
        if(other?.javaClass != this.javaClass) return false
        other as Closure
        return items == other.items
    }
    override fun hashCode() = items.hashCode()
}