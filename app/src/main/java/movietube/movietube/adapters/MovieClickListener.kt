package movietube.movietube.adapters

import android.view.View
import movietube.movietube.pojo.Movie

interface MovieClickListener {
    fun onMovieClick(item: View, position: Int, targetMovie: Movie)
}
