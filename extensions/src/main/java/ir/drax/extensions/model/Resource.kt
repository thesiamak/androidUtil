package ir.drax.extensions.model

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val errorCode : Int? = 0) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T? , errorCode: Int? = 0): Resource<T> {
            return Resource(Status.ERROR, data, msg ,errorCode)
        }

        fun <T> loading(data: T?=null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}
