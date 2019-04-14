package movietube.movietube.pages

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.movie_page.view.*
import movietube.movietube.R
import movietube.movietube.adapters.MovieAdapter
import movietube.movietube.adapters.MovieClickListener
import movietube.movietube.home.MainActivity
import movietube.movietube.listeners.OnFragmentInteraction
import movietube.movietube.pojo.Movie

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MoviePage : Fragment() {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val moviePageContainer = inflater.inflate(R.layout.movie_page, container, false)
        val movieList: ArrayList<Movie> = ArrayList()
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        movieList.add(Movie("Foo bar", "", 2.2f, true, ""))
        moviePageContainer.movieRecyclerView.adapter = MovieAdapter(movieList, object : MovieClickListener {
            override fun onMovieClick(item: View, position: Int, targetMovie: Movie) {
                var home = context as MainActivity
                home.playVideo(Movie("Foo bar", "", 2.2f, true, ""))
                home.suggestedMovie(movieList)

            }

        })
        moviePageContainer.movieRecyclerView.layoutManager = LinearLayoutManager(context, OrientationHelper.VERTICAL, false)
        return moviePageContainer
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
