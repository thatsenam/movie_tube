package movietube.movietube.home

import movietube.movietube.base.BasePresenter
import movietube.movietube.base.BaseView
import movietube.movietube.listeners.Listener
import movietube.movietube.pojo.*


interface MainContact {
    interface View : BaseView {
        fun playVideo(movie: Movie, link: Link)
        fun hideBottomSheet()
        fun initPlayer()
        fun defaultConfigure()
        fun suggestedMovie(movies: List<Movie>)
        fun configureBottomSheet()
        fun fullScreen()
        fun normalScreen()
        fun miniScreen()
        fun playLive(liveTv: LiveTv, link: Link)
    }

    interface Presenter : BasePresenter {
        fun loadMoreSuggestion(channel: Int, page: Int, listener: Listener<List<Movie>>)
        fun onViewClicked(view: android.view.View)

    }

    interface Model {
        fun getMovies(page: Int = 0, listener: Listener<List<Movie>>?)
        fun getTrending(page: Int = 0, listener: Listener<List<Movie>>)
        fun getChannels(listener: Listener<List<Channel>>?)
        fun getLiveTv(page: Int, listener: Listener<List<LiveTv>>)
        fun putView(movie: Movie)
        fun getSuggestion(channel: Int, page: Int, listener: Listener<List<Movie>>)
        fun getLiveSuggestion(page: Int, listener: Listener<List<LiveTv>>)
        fun getComments(page: Int = 0, type: String, id: Int, listener: Listener<List<Comment>>)
    }


}