package movietube.movietube.home

import android.content.Context
import movietube.movietube.Listener
import movietube.movietube.pojo.Movie

class Model(var context: Context) : MainContact.Model {

    override fun getMovies(limit: Int, listener: Listener<List<Movie>>) {

    }


}