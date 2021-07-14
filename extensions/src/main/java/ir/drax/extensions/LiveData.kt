package ir.drax.extensions

import androidx.lifecycle.*


/**
 * Map on MutableLiveData return value
 */
public inline fun <X, Y> LiveData<X>.map(crossinline transform: (X) -> Y): MutableLiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(this) {
        result.postValue(transform(it))
    }
    return result
}



/**
 * Manual dispatch on LiveData objects
 */
public fun <X> MutableLiveData<X>.dispatch() {
    postValue(value)
}


/**
 * Combines multiple livedata objects and fires @param(block) each time a livedata is updated.
 * This is a brief version of MediatorLiveData.
 * @sample
 */

fun <E, T, R> LiveData<T>.mergeLiveData(viewLifecycleOwner: LifecycleOwner, source: LiveData<E>, block: (T, E) -> R): MediatorLiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(source) {
        it?.let { sourceValue ->
            this.value?.let { originValue ->
                result.postValue(block(originValue, sourceValue))
            }
        }
    }
    result.addSource(this) {
        it?.let { originValue ->
            source.value?.let { sourceValue ->
                result.postValue(block(originValue, sourceValue))
            }
        }
    }

    result.observe(viewLifecycleOwner) {}

    return result
}

fun <E, T, R> LiveData<T?>.mergeNonNullLiveData(viewLifecycleOwner: LifecycleOwner, source: LiveData<E?>, block: (T, E) -> R): MediatorLiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(source) {
        it?.let { sourceValue ->
            this.value?.let { originValue ->
                result.postValue(block(originValue, sourceValue))
            }
        }
    }
    result.addSource(this) {
        it?.let { originValue ->
            source.value?.let { sourceValue ->
                result.postValue(block(originValue, sourceValue))
            }
        }
    }

    result.observe(viewLifecycleOwner) {}

    return result
}
