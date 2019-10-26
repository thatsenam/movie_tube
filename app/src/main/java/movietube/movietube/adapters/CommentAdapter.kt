package movietube.movietube.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comment_item.view.*
import movietube.movietube.App
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.listeners.Listener
import movietube.movietube.model.Model
import movietube.movietube.pojo.Comment
import movietube.movietube.pojo.Post
import org.ocpsoft.prettytime.PrettyTime


class CommentAdapter(
    var context: Context?,
    var commentList: MutableList<Comment>,
    var listener: Listeners.OnCommentItemClickListener,
    var model: Model = Model(context!!)
) : RecyclerView.Adapter<CommentAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        var comment = this.commentList.get(position)
        Log.w("track", " ${comment.id} : ${comment.p_id} : ${comment.Body}")
        val author = if (comment.Author == null || comment.Author.equals("")) "anonymous" else comment.Author;
        val time = PrettyTime().format(if (comment.PostDate != null) comment.PostDate else App.getRandomDate())
        val replyRV = holder.itemView.comment_reply_rv
        holder.itemView.comment_body.text = comment.Body
        holder.itemView.comment_info.text = "$author • $time"
        holder.itemView.voteTV.text = comment.Votes.toString()
        if (comment.Replies > 0) {
            holder.itemView.comment_report.text = comment.Replies.toString()
            var layoutManager = object : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

            replyRV.layoutManager = layoutManager

            val adapter = CommentAdapter(context, mutableListOf(), object : Listeners.OnCommentItemClickListener {
                override fun onCommentClick(item: View, position: Int, post: Post) {

                }

                override fun onReplyClick(item: View, position: Int, post: Post) {

                }

            })
            replyRV.adapter = adapter
            model.getComments(0, "comment", comment.id, object : Listener<List<Comment>>() {
                override fun onSuccess(response: List<Comment>) {
                    adapter.addItem(response.toMutableList())
                }

                override fun onError(error: String) {
                    Toast.makeText(context, "CommentAdapter : $error", Toast.LENGTH_SHORT).show()
                }

            })

        }
        holder.itemView.requestLayout()
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
