package movietube.movietube.pages

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_stack_overflow.view.*
import movietube.movietube.Listeners
import movietube.movietube.adapters.StackAdapter
import movietube.movietube.custom.EditNameDialogFragment
import movietube.movietube.home.MainActivity
import movietube.movietube.interfaces.ApiService
import movietube.movietube.listeners.OnFragmentInteraction
import movietube.movietube.pojo.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StackOverflow : Fragment(), Listeners.OnStackItemClickListener {

    override fun onStakItemClick(item: View, position: Int, post: Post) {
        showEditDialog(post, item)
    }

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(movietube.movietube.R.layout.fragment_stack_overflow, container, false)
        view.stack_progress.visibility = View.VISIBLE
        var layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var retrofit = Retrofit.Builder().baseUrl(context?.resources?.getString(movietube.movietube.R.string.api_url)!!)
            .addConverterFactory(GsonConverterFactory.create()).build()
        var postList = retrofit.create(ApiService::class.java).postList()
        var stackAdapter = StackAdapter(context, mutableListOf(), this)
        view.stack_rv.layoutManager = layoutManager
        view.stack_rv.adapter = stackAdapter
        postList.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                view.stack_progress.visibility = View.GONE
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.code() == 200) {
                    view.stack_progress.visibility = View.GONE
                    val stackList = response.body()

                    stackAdapter.addItem(response.body()?.toMutableList()!!)
                } else {
                    Toast.makeText(context, "Error: Response Code ${response.code()}", Toast.LENGTH_LONG).show()
                }

            }

        })
        return view
    }


    private fun showEditDialog(post: Post, view: View) {
        val fm = (context as MainActivity).supportFragmentManager
        val editNameDialogFragment = EditNameDialogFragment.newInstance(post, view)
        editNameDialogFragment.show(fm, "qa")
    }

    fun onButtonPressed(view: View) {
        listener?.onFragmentItemClick(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StackOverflow().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
