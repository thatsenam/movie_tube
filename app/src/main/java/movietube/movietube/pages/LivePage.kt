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
import kotlinx.android.synthetic.main.upload_page.view.*
import movietube.movietube.R
import movietube.movietube.adapters.LiveAdapter
import movietube.movietube.home.MainActivity
import movietube.movietube.model.Model
import movietube.movietube.listeners.Listener
import movietube.movietube.listeners.OnFragmentInteraction
import movietube.movietube.pojo.LiveTv

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UploadPage : Fragment() {
    // TODO: Rename and change types of parameters
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
        var model: Model = Model(context!!)
        var activity = context as MainActivity
        var view = inflater.inflate(R.layout.upload_page, container, false)
        var liveRv = view.live_rv
        var liveAdapter = LiveAdapter(context!!, mutableListOf(), object : LiveAdapter.ItemClickListener {
            override fun onItemClick(view: View, liveTv: LiveTv) {
                activity.playLive(liveTv, liveTv.Links?.get(0)!!)
            }

        })
        liveRv.adapter = liveAdapter
        var linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        liveRv.layoutManager = linearLayoutManager
        view.live_progress.visibility = View.VISIBLE
        model.getLiveTv(0, listener = object : Listener<List<LiveTv>>() {
            override fun onSuccess(response: List<LiveTv>) {
                // toast(response.toString())
                liveAdapter.addLives(response.toMutableList())
                view.live_progress.visibility = View.GONE
            }

            override fun onError(error: String) {
                toast(error)
                view.live_progress.visibility = View.GONE

            }

        })
        val scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                model.getLiveTv(page, listener = object : Listener<List<LiveTv>>() {

                    override fun onSuccess(response: List<LiveTv>) {
                        liveAdapter.addLives(response.toMutableList())
                    }

                    override fun onError(error: String) {
                        toast(error)

                    }

                })
            }
        }
        liveRv.addOnScrollListener(scrollListener)
        view.live_swipe.setOnRefreshListener {
            scrollListener.resetState()
            model.getLiveTv(0, listener = object : Listener<List<LiveTv>>() {
                override fun onSuccess(response: List<LiveTv>) {
                    view.live_swipe.isRefreshing = false
                    liveAdapter.addLives(response.toMutableList())
                }

                override fun onError(error: String) {
                    toast(error)
                    view.live_swipe.isRefreshing = false

                }

            })
        }


        return view
    }

    fun onButtonPressed(view: View) {
        listener?.onFragmentItemClick(view)
    }

    fun toast(mgs: String) {
        Toast.makeText(context, mgs, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteraction) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
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
            UploadPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
