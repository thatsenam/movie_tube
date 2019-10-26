package movietube.movietube.home

import android.view.View
import movietube.movietube.listeners.Listener
import movietube.movietube.pojo.Movie


class MainPresenter(var view: MainContact.View, var model: MainContact.Model) : MainContact.Presenter {

    init {

    }

    override fun loadMoreSuggestion(channel: Int, page: Int, listener: Listener<List<Movie>>) {
        model.getSuggestion(channel, page, listener)
    }

    override fun onViewClicked(view: View) {

    }

    override fun enqueue() {
        view.initPlayer()
        view.defaultConfigure()
        view.configureBottomSheet()
    }

    override fun viewClicked(view: View) {

    }


}

