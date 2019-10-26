package movietube.movietube.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.movie_suggested_item.view.*
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.pojo.Movie


class SuggestedAdapter(var movieList: MutableList<Movie>, var listener: Listeners.MovieClickListener) :
    RecyclerView.Adapter<SuggestedAdapter.MovieVh>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MovieVh {
        return MovieVh(LayoutInflater.from(viewGroup.context).inflate(R.layout.movie_suggested_item, viewGroup, false))

    }

    override fun onBindViewHolder(holder: MovieVh, i: Int) {
        var movie = movieList[i]
        holder.itemView.setOnClickListener {
            listener.onMovieClick(it, i, movie)
        }
        Glide.with(holder.itemView.context).load(movie.Poster).into(holder.itemView.movieThumb)
        holder.itemView.movieTitle.text = movie.Title

    }
    fun addMovie(movies: MutableList<Movie>) {
        movieList.addAll(movies)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieVh(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}
