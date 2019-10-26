package movietube.movietube.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comment_item.view.*
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.model.Model
import movietube.movietube.pojo.Comment
import org.ocpsoft.prettytime.PrettyTime


class ReplyAdapter(
    var context: Context?,
    var commentList: MutableList<Comment>,
    var listener: Listeners.OnCommentItemClickListener,
    var model: Model = Model(context!!)
) : RecyclerView.Adapter<ReplyAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        var comment = this.commentList.get(position)
        val author = if (comment.Author == null || comment.Author.equals("")) "anonymous" else comment.Author;
        val time = PrettyTime().format(comment.PostDate)
        holder.itemView.comment_body.text = comment.Body
        holder.itemView.comment_info.text = "$author • $time"
        holder.itemView.voteTV.text = comment.Votes.toString()

    }

    fun addItem(items: MutableList<Comment>) {
        commentList.addAll(items)
        notifyDataSetChanged()


    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}
