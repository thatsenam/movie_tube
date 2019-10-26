package movietube.movietube.pages


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.movie_page.view.*
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

class MoviePage : Fragment() {


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
        var model = Model(container!!.context)
        val mc = inflater.inflate(R.layout.movie_page, container, false)
        var swipeRefreshLayout = mc.findViewById<SwipeRefreshLayout>(R.id.swipe)
        var layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        var lmProgress = mc.findViewById<ProgressBar>(R.id.load_more_progress)
        var progress = mc.findViewById<ProgressBar>(R.id.progress_bar)


        var movieAdapter = MovieAdapter(mutableListOf(), object : Listeners.MovieClickListener {
            override fun onMovieClick(item: View, position: Int, targetMovie: Movie) {
                var home = context as MainActivity
                home.playVideo(targetMovie, targetMovie.Links?.get(0)!!)
            }

        }, MainActivity.activity)
        var endlessScrollListener = object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                //  Toast.makeText(context, "Load More", Toast.LENGTH_SHORT).show()
                lmProgress.visibility = View.VISIBLE

                model.getMovies(page, object : Listener<List<Movie>>() {
                    override fun onSuccess(response: List<Movie>) {
                        lmProgress.visibility = View.GONE
                        movieAdapter.addMovie(response.toMutableList())
                    }

                    override fun onError(error: String) {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        lmProgress.visibility = View.GONE
                    }
                })


            }


        }
        mc.movieRecyclerView.addOnScrollListener(endlessScrollListener)
        mc.movieRecyclerView.layoutManager = layoutManager
        mc.movieRecyclerView.adapter = movieAdapter
        mc.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
        model.getMovies(0, object : Listener<List<Movie>>() {
            override fun onSuccess(response: List<Movie>) {
                swipeRefreshLayout.isRefreshing = false
                progress.visibility = View.GONE
                movieAdapter.addMovie(response.toMutableList())
            }

            override fun onError(error: String) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                progress.visibility = View.GONE
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            mc.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
            mc.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
            model.getMovies(0, object : Listener<List<Movie>>() {
                override fun onSuccess(response: List<Movie>) {
                    swipeRefreshLayout.isRefreshing = false
                    progress.visibility = View.GONE
                    movieAdapter.clear()
                    movieAdapter.addMovie(response.toMutableList())
                    Log.w("w", "response : ${response.size}")
                    endlessScrollListener.resetState()
                }

                override fun onError(error: String) {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    progress.visibility = View.GONE

                }
            })


        }

        return mc
    }


    fun onButtonPressed(view: View) {
        listener?.onFragmentItemClick(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteraction) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MoviePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoviePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
