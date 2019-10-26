package movietube.movietube.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.home.MainContact
import movietube.movietube.model.Model
import movietube.movietube.pojo.Comment


class MovieCommentAdapter(
    var context: Context?,
    var commentList: MutableList<Comment>,
    var listener: Listeners.OnCommentItemClickListener,
    var model: MainContact.Model = Model(context!!)
) : RecyclerView.Adapter<MovieCommentAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.movie_comment_item, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

    }

    fun addItem(items: MutableList<Comment>) {
        commentList.addAll(items)
        notifyDataSetChanged()


    }

    fun clearAll() {
        commentList.clear()
        notifyDataSetChanged()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}
