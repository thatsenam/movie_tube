package movietube.movietube.adapters

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startapp.android.publish.ads.banner.Banner
import com.startapp.android.publish.ads.banner.Mrec
import com.startapp.android.publish.ads.nativead.NativeAdPreferences
import com.startapp.android.publish.ads.nativead.StartAppNativeAd
import com.startapp.android.publish.adsCommon.Ad
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener
import kotlinx.android.synthetic.main.movie_item.view.*
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.pojo.Movie


class TrendingMovieAdapter(var movieList: MutableList<Movie>, var listener: Listeners.MovieClickListener, var activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var ad_type = 0
    var content_type = 1


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        var context = viewGroup.context

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

        if (position % 5 == 0)
            return ad_type
        return content_type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (holder.itemViewType == ad_type) {
            return
        }

        holder.itemView.setOnClickListener {
            listener.onMovieClick(it, i, movieList[i])
        }
        Glide.with(holder.itemView.context).load(movieList[i].Poster).into(holder.itemView.movieThumb)
        holder.itemView.movieTitle.text = movieList[i].Title
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    fun addMovie(movies: MutableList<Movie>) {
        movieList.addAll(movies)
        notifyDataSetChanged()
    }


    inner class MovieVh(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    inner class AdVH(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }
}
