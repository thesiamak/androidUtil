package ir.drax.expandable

import java.util.*

class StateObservable(state:Boolean) : Observable(){

    var state:Boolean=false
        set(newState) {
            setChanged()
            notifyObservers(newState)
            field=newState
        }
}