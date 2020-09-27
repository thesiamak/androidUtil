package ir.drax.expandable

import java.util.*

class StateObservable(default:Boolean) : Observable(){
    var state:Boolean=false
        set(newState) {
            field=newState
            setChanged()
            notifyObservers(newState)
        }
    init {
        state=default
    }
}