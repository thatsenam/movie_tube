package movietube.movietube


import android.view.View
import movietube.movietube.pojo.LiveTv
import movietube.movietube.pojo.Movie
import movietube.movietube.pojo.Post

interface Listeners {

    interface MovieClickListener {
        fun onMovieClick(item: View, position: Int, targetMovie: Movie)
    }

    interface LiveListener {
        fun onLiveClick(item: View, position: Int, live: LiveTv)
    }

    interface OnStackItemClickListener {
        fun onStakItemClick(item: View, position: Int, post: Post)
    }
    interface OnCommentItemClickListener {
        fun onCommentClick(item: View, position: Int, post: Post)
        fun onReplyClick(item: View, position: Int, post: Post)
    }
}


