package movietube.movietube.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import movietube.movietube.R
import movietube.movietube.pojo.Movie

class MovieAdapter(var movieList: List<Movie>, var listener: MovieClickListener) : RecyclerView.Adapter<MovieAdapter.MovieVh>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MovieVh {
        return MovieVh(LayoutInflater.from(viewGroup.context).inflate(R.layout.movie_item, viewGroup, false))

    }

    override fun onBindViewHolder(movieVh: MovieVh, i: Int) {
        movieVh.itemView.setOnClickListener {
            listener.onMovieClick(it, i, movieList[i])
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieVh(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}
