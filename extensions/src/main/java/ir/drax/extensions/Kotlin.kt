package ir.drax.extensions


/**
 *  Null safe multi operator function
 */
fun <E : Any?, T, R> E.ifNotNull(vararg b: T?, block: () -> R): R? {
    return if (this != null) {
        if (b.all { it != null })
            block()
        else null
    } else
        return null
}