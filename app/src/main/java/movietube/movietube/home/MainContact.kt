package movietube.movietube.home

import movietube.movietube.Listener
import movietube.movietube.base.BasePresenter
import movietube.movietube.base.BaseView
import movietube.movietube.pojo.Movie

interface MainContact {
    interface View : BaseView {
        fun playVideo(url: Movie)
        fun hideBottomSheet()
        fun initPlayer()
        fun defaultConfigure()
        fun suggestedMovie(movies: List<Movie>);
    }

    interface Presenter : BasePresenter<View> {
        fun enqueue()
        fun viewClicked(view: android.view.View)
        fun loadMovies()
    }

    interface Model {
        fun getMovies(limit: Int = 20, listener: Listener<List<Movie>>)
    }


}