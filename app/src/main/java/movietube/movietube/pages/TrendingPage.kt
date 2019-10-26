package movietube.movietube.pages


import android.content.Context
import android.net.Uri
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.treding_page.view.*
import movietube.movietube.Listeners
import movietube.movietube.R
import movietube.movietube.adapters.MovieAdapter
import movietube.movietube.home.MainActivity
import movietube.movietube.model.Model
import movietube.movietube.listeners.Listener
import movietube.movietube.listeners.OnFragmentInteraction
import movietube.movietube.pojo.Movie


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TrendingPage : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteraction? = null
    private var contexto: Context? = null

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
        var model = Model(container!!.context)
        var view = inflater.inflate(R.layout.treding_page, container, false)
        var swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe)
        var layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var trendingRV = view.findViewById<RecyclerView>(R.id.trendingRV)
        var loadMore = view.findViewById<ProgressBar>(R.id.trending_load_more_progress)
        trendingRV.layoutManager = layoutManager
        context?.let {
            view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
            model.getTrending(20, object : Listener<List<Movie>>() {
                override fun onSuccess(response: List<Movie>) {
                    swipeRefreshLayout.isRefreshing = false
                    view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
                    view.trendingRV.layoutManager = layoutManager

                    view.trendingRV.adapter =
                        MovieAdapter(response.toMutableList(), object : Listeners.MovieClickListener {
                            override fun onMovieClick(item: View, position: Int, targetMovie: Movie) {
                                var home = context as MainActivity
                                if (targetMovie != null) {
                                    home.playVideo(targetMovie,targetMovie.Links?.get(0)!!)
                                }
                               // home.suggestedMovie(response)

                            }

                        }, MainActivity.activity)
                }

                override fun onError(error: String) {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

                }
            })
        }

        swipeRefreshLayout.setOnRefreshListener {
            view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
            context?.let {
                view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
                Model(it).getTrending(20, object : Listener<List<Movie>>() {
                    override fun onSuccess(response: List<Movie>) {
                        swipeRefreshLayout.isRefreshing = false
                        view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
                        view.trendingRV.layoutManager = layoutManager
                        view.trendingRV.adapter =
                            MovieAdapter(response.toMutableList(), object : Listeners.MovieClickListener {
                                override fun onMovieClick(item: View, position: Int, targetMovie: Movie) {
                                    var home = context as MainActivity
                                    home.playVideo(targetMovie,targetMovie.Links?.get(0)!!)
                                  //  home.suggestedMovie(response)
                                }
                            }, MainActivity.activity)
                    }

                    override fun onError(error: String) {
                        swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

                    }
                })
            }

        }
        /*var endlessScrollListener = object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
*//*
                Toast.makeText(container?.context, "Load More", Toast.LENGTH_SHORT).show()
*//*
                loadMore.visibility = View.VISIBLE
                Handler().postDelayed(
                    {
                        loadMore.visibility = View.GONE

                    }, 10000
                )
            }


        }
        view.trendingRV.addOnScrollListener(endlessScrollListener)*/
        return view
    }

    fun onButtonPressed(view: View) {
        listener?.onFragmentItemClick(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.contexto = context
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
            TrendingPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
