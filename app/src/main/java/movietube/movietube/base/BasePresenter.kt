package movietube.movietube.base

interface BasePresenter {
    fun enqueue()
    fun viewClicked(view: android.view.View)
}