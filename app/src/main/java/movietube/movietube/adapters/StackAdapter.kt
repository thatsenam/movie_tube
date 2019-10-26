package movietube.movietube.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.stack_item.view.*
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.pojo.Post
import org.ocpsoft.prettytime.PrettyTime


class StackAdapter(
    var context: Context?,
    var stackList: MutableList<Post>,
    var listener: Listeners.OnStackItemClickListener
) : RecyclerView.Adapter<StackAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.stack_item, parent, false))
    }

    override fun getItemCount(): Int {
        return stackList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        var post = this.stackList.get(position)

        holder.itemView.stack_title.text = post.Title
        holder.itemView.stack_body.text = post.Body
        holder.itemView.voteTV.text = if (post.Votes == 0) "Vote" else post.Votes.toString()
        holder.itemView.stack_comments.text = if (post.Replies == 0) "Comment" else post.Replies.toString()
        val author = if (post.Author == null || post.Author.equals("")) "anonymous" else post.Author;
        val time = PrettyTime().format(post.PostDate)
        val type = if (post.Type.equals("question")) "Question" else "Movie Request"
        holder.itemView.stack_type.text = "r/$type"
        holder.itemView.stack_info.text = "Posted by u/$author • $time"
        holder.itemView.upvoteBtn.setOnClickListener {
            holder.itemView.downVoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_down_black
                )
            )
            holder.itemView.upvoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_up_blue
                )
            )
            post.Votes = post.Votes.plus(1)
            holder.itemView.voteTV.text = post.Votes.toString()
            holder.itemView.downVoteBtn.isEnabled = true
            holder.itemView.upvoteBtn.isEnabled = false
        }
        holder.itemView.downVoteBtn.setOnClickListener {
            holder.itemView.upvoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_up_black
                )
            )
            holder.itemView.downVoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_down_blue
                )
            )
            post.Votes = post.Votes.minus(1)
            holder.itemView.voteTV.text = post.Votes.toString()
            holder.itemView.downVoteBtn.isEnabled = false
            holder.itemView.upvoteBtn.isEnabled = true
        }
        holder.itemView.setOnClickListener {
            listener.onStakItemClick(holder.itemView, position, post)
        }


    }

    fun addItem(items: MutableList<Post>) {
        stackList.addAll(items)
        notifyDataSetChanged()


    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}
