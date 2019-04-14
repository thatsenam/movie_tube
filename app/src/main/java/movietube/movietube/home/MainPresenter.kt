package movietube.movietube.home

import android.view.View
import movietube.movietube.Listener
import movietube.movietube.base.BasePresenterImpl
import movietube.movietube.pojo.Movie

class MainPresenter(var model: MainContact.Model) : BasePresenterImpl<MainContact.View>(), MainContact.Presenter {


    override fun loadMovies() {
        model.getMovies(listener = object : Listener<List<Movie>>() {
            override fun onError(error: String) {

            }

            override fun onSuccess(response: List<Movie>) {

            }

        })
    }

    override fun enqueue() {
        view?.initPlayer()
        view?.defaultConfigure()
    }

    override fun viewClicked(view: View) {

    }

}

