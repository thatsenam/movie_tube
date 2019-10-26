package movietube.movietube.custom


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.qa_details_item.view.*
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.adapters.CommentAdapter
import movietube.movietube.listeners.Listener
import movietube.movietube.model.Model
import movietube.movietube.pojo.Comment
import movietube.movietube.pojo.Post
import org.ocpsoft.prettytime.PrettyTime

class EditNameDialogFragment(var post: Post, var bearerView: View) : DialogFragment() {

    private val mEditText: EditText? = null
    private val model: Model

    init {
        model = Model(bearerView.context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.qa_details_item, container)
        view.pop_stack_title.text = post.Title
        view.pop_stack_body.text = post.Body
        view.popvoteTV.text = if (post.Votes == 0) "Vote" else post.Votes.toString()
        view.pop_stack_comments.text = if (post.Replies == 0) "Comment" else post.Replies.toString()
        val author = if (post.Author == null || post.Author.equals("")) "anonymous" else post.Author;
        val time = PrettyTime().format(post.PostDate)
        val type = if (post.Type.equals("question")) "Question" else "Movie Request"
        view.pop_stack_type.text = "r/$type"
        view.pop_stack_info.text = "Posted by u/$author • $time"
        view.popupvoteBtn.setOnClickListener {
            view.popdownVoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_down_black
                )
            )
            view.popupvoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_up_blue
                )
            )
            post.Votes = post.Votes.plus(1)
            view.popvoteTV.text = post.Votes.toString()
            view.popdownVoteBtn.isEnabled = true
            view.popupvoteBtn.isEnabled = false
        }
        view.popdownVoteBtn.setOnClickListener {
            view.popupvoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_up_black
                )
            )
            view.popdownVoteBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_down_blue
                )
            )
            post.Votes = post.Votes.minus(1)
            view.popvoteTV.text = post.Votes.toString()
            view.popdownVoteBtn.isEnabled = false
            view.popupvoteBtn.isEnabled = true
        }

        view.qa_close.setOnClickListener {
            dialog?.hide()
        }
        var adapter =
            CommentAdapter(context, mutableListOf(), object : Listeners.OnCommentItemClickListener {
                override fun onCommentClick(item: View, position: Int, post: Post) {
                    toast("Comment Item Clicked")
                }

                override fun onReplyClick(item: View, position: Int, post: Post) {
                    toast("Reply Item Clicked")

                }

            })
        view.commentRV.adapter = adapter
        view.commentRV.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        view.commentRV.isNestedScrollingEnabled = true
        if (post.Replies > 0) {

            view.comment_swipe.setOnRefreshListener {

                model.getComments(0, "topic", post.id, object : Listener<List<Comment>>() {
                    override fun onSuccess(response: List<Comment>) {
                        // toast(response.toString())
                        view.comment_swipe.isRefreshing = false
                        if (response.isEmpty()) {
                            view.qa_no_comment.visibility = View.VISIBLE
                        } else {
                            adapter.clearAll()
                            adapter.addItem(response.toMutableList())
                            view.qa_no_comment.visibility = View.GONE
                            //  toast(response.toString())
                        }

                    }

                    override fun onError(error: String) {
                        toast("error " + error)
                        view.comment_swipe.isRefreshing = false
                    }

                })
            }

            model.getComments(0, "topic", post.id, object : Listener<List<Comment>>() {
                override fun onSuccess(response: List<Comment>) {
                    // toast(response.toString())
                    if (response.isEmpty()) {
                        view.qa_no_comment.visibility = View.VISIBLE
                    } else {
                        adapter.addItem(response.toMutableList())
                        view.qa_no_comment.visibility = View.GONE
                        //  toast(response.toString())
                    }

                }

                override fun onError(error: String) {
                    toast("error " + error)
                }

            })
        } else {
            toast("No Comments To Load")
            view.qa_no_comment.visibility = View.VISIBLE
        }



        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog?.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    }

    fun toast(toast: String) {
        Toast.makeText(bearerView.context, toast, Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun newInstance(post: Post, view: View): EditNameDialogFragment {
            val frag = EditNameDialogFragment(post, view)
            frag.setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar)
            return frag
        }
    }
}

