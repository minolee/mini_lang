package parser

import structure.Keyword

class Partition internal constructor(items : Collection<Item>) {
    val items = HashSet<Item>()
    val shift = HashMap<Keyword, Partition>()
    init
    {
        this.items.addAll(items)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        items.forEach {builder.append(it)}
        return builder.toString()
    }
}