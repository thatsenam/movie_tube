package movietube.movietube.listeners


abstract class Listener<T> {
    abstract fun onSuccess(response: T)
    abstract fun onError(error: String)

}
