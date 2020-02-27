package ru.gordinmitya.dnnbenchmark.utils

class CyclicIterator<T>(val source: Collection<T>) : Iterator<T> {
    val isEmpty = source.isNotEmpty()
    var iterator = source.iterator()

    override fun hasNext(): Boolean {
        return isEmpty
    }

    override fun next(): T {
        if (!iterator.hasNext())
            iterator = source.iterator()
        return iterator.next()
    }
}