package movietube.movietube.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startapp.android.publish.ads.banner.Mrec
import com.startapp.android.publish.ads.nativead.StartAppNativeAd
import kotlinx.android.synthetic.main.movie_item.view.*
import movietube.movietube.Listeners
import movietube.movietube.MovieTubeUtils
import movietube.movietube.R
import movietube.movietube.pojo.Channel
import movietube.movietube.pojo.Movie
import org.ocpsoft.prettytime.PrettyTime


class MovieAdapter(var movieList: MutableList<Movie?>, var listener: Listeners.MovieClickListener, var activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var ad_type = 0
    var content_type = 1
    var adAttached = false
    lateinit var context: Context

    init {

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        var context = viewGroup.context
        this.context = context
        if (i == ad_type) {
            var layout = LinearLayout(context)
            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layout.gravity = Gravity.CENTER
            var banner = Mrec(activity)
            var nativeAd = StartAppNativeAd(context)
            nativeAd.loadAd()
            banner.gravity = Gravity.CENTER
            layout.addView(banner)
            return AdVH(layout)

        } else {
            return MovieVh(LayoutInflater.from(context).inflate(R.layout.movie_item, viewGroup, false))

        }

    }

    override fun getItemViewType(position: Int): Int {

        if (position == 1)
            return ad_type
        return content_type
    }

    fun getChannel(i: Int?): Channel? {
        Log.e("loop", "position ? $i")
        Log.e("loop", MovieTubeUtils.channels.toString())
        for (channel in MovieTubeUtils.channels) {
            if (channel.id == i) {
                return channel
            }
        }

        return null
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        var prettyTime = PrettyTime()

        if (holder.itemViewType == ad_type) {
            return
        }
        var channel = getChannel(movieList[i]?.ChannelID)
        var movie = movieList[i]
        var time = prettyTime.format(movie?.PostDate)
        holder.itemView.setOnClickListener {
            movieList[i]?.let { it1 -> listener.onMovieClick(it, i, it1) }
        }
        Glide.with(holder.itemView.context).load(movieList[i]?.Poster).into(holder.itemView.movieThumb)
        holder.itemView.movieTitle.text = movieList[i]?.Title
        holder.itemView.movieChannel.text =
            "${channel?.Title} • ${movie?.Views} views • $time"
        Glide.with(holder.itemView.context).load(channel?.Poster).into(holder.itemView.movieChannelThum)
        holder.itemView.movie_options.setOnClickListener {
            showPopup(it)
        }

    }

    fun showPopup(v: View) {
        val popup = PopupMenu(context, v)
        val inflater = popup.getMenuInflater()
        popup.setOnMenuItemClickListener { item ->
            Toast.makeText(context, "${item?.title}", Toast.LENGTH_SHORT).show()
            true
        }
        inflater.inflate(R.menu.movie_option, popup.getMenu())
        popup.show()
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    fun addMovie(movies: MutableList<Movie>) {
        movieList.addAll(movies)
        if (!adAttached && movies.size > 0) {
            movieList.add(1, null)
            adAttached = true
        }
        notifyDataSetChanged()
    }

    fun clear() {
        adAttached = false
        movieList.clear()
        // Log.w("w", "Sie : ${lives.size}")
    }

    inner class MovieVh(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    inner class AdVH(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }
}
