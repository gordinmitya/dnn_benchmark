package ru.gordinmitya.common.classification

class GTSampler(private val samples: List<GT>) : Iterator<GT> {
    private var iterator: Iterator<GT> = samples.iterator()

    init {
        if (samples.isEmpty())
            throw IllegalArgumentException("Can't work without examples!")
    }

    override fun next(): GT {
        if (!iterator.hasNext())
            iterator = samples.iterator()
        return iterator.next()
    }

    override fun hasNext(): Boolean = true
}