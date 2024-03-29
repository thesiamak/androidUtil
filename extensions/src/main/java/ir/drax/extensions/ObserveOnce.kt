package ir.drax.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer:(T?)->Unit) {
    observe(lifecycleOwner,object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer(t)
        }
    })
}

fun <T> LiveData<T?>.observeOnceNotNull(lifecycleOwner: LifecycleOwner, observer:(T)->Unit) {
    observe(lifecycleOwner,object : Observer<T?> {
        override fun onChanged(t: T?) {
            t?.let {
                removeObserver(this)
                observer(t)
            }
        }
    })
}

fun <T> LiveData<T>.observeOnceNotNull(lifecycleOwner: LifecycleOwner, observer:Observer<T>) {
    observe(lifecycleOwner,object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                removeObserver(this)
                observer.onChanged(t)
            }
        }
    })
}