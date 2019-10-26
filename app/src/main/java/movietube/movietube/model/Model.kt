package movietube.movietube.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonElement
import movietube.movietube.MovieTubeUtils
import movietube.movietube.R
import movietube.movietube.home.MainContact
import movietube.movietube.interfaces.ApiService
import movietube.movietube.listeners.Listener
import movietube.movietube.pojo.Channel
import movietube.movietube.pojo.Comment
import movietube.movietube.pojo.LiveTv
import movietube.movietube.pojo.Movie
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Model(var context: Context) : MainContact.Model {


    var isProcessing = false
    var channelListener: Listener<List<Channel>>? = null
    var liveTvListener: Listener<List<LiveTv>>? = null
    var movieListener: Listener<List<Movie>>? = null
    var movies = MovieTubeUtils.movies
    var channels = MovieTubeUtils.channels
    var lives = MovieTubeUtils.lives
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var retrofit: Retrofit
    private val cacheSize = (5 * 1024 * 1024).toLong()
    private val myCache = Cache(context.cacheDir, cacheSize)

    init {
        val okHttpClient = OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(context)!!)
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).addHeader(
                        "Content-Type",
                        "application/json"
                    ).build()
                else
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).addHeader("Content-Type", "application/json").build()


                chain.proceed(request)
            }
            .build()
        retrofit = Retrofit.Builder().baseUrl(context.resources.getString(R.string.api_url))
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
        getChannels(null)
    }


    fun GMListner(): Listener<List<Movie>>? {
        return this.movieListener
    }

    override fun getComments(page: Int, type: String, id: Int, listener: Listener<List<Comment>>) {
        retrofit.create(ApiService::class.java).getComments(id, type).enqueue(object : Callback<List<Comment>?> {
            override fun onFailure(call: Call<List<Comment>?>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Comment>?>, response: Response<List<Comment>?>) {
                if (response.body() == null) {
                    listener.onError("Empty Response")
                } else {
                    listener.onSuccess(response.body()!!)
                }
            }

        })
    }

    fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }

    override fun getChannels(listener: Listener<List<Channel>>?) {
        this.channelListener = listener
        if (MovieTubeUtils.channels.isEmpty()) {
            var channelOberservable = retrofit.create(ApiService::class.java).getChannel()
            channelOberservable.enqueue(object : Callback<List<Channel>?> {
                override fun onFailure(call: Call<List<Channel>?>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<List<Channel>?>, response: Response<List<Channel>?>) {
                    MovieTubeUtils.channels = response.body()!!
                    listener?.onSuccess(MovieTubeUtils.channels)
                }
            })

        } else {
            this.channelListener?.onSuccess(MovieTubeUtils.channels)
        }
    }

    override fun getLiveTv(page: Int, listener: Listener<List<LiveTv>>) {
        this.liveTvListener = listener
        if (MovieTubeUtils.lives.isEmpty()) {
            var liveOberserver = retrofit.create(ApiService::class.java).getLives()
            liveOberserver.enqueue(object : Callback<List<LiveTv>?> {
                override fun onFailure(call: Call<List<LiveTv>?>, t: Throwable) {
                    listener.onError(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<LiveTv>?>, response: Response<List<LiveTv>?>) {
                    MovieTubeUtils.lives = response.body()!!
                    listener.onSuccess(response.body()!!)
                }

            })
        } else {
            listener.onSuccess(MovieTubeUtils.lives)
        }
    }

    fun log(msg: String) {
        Log.e("enam", msg)

    }

    override fun getMovies(page: Int, listener: Listener<List<Movie>>?) {
        var movieObservable = retrofit.create(ApiService::class.java).getMovies(page)
        if (MovieTubeUtils.channels.isEmpty()) {
            var channelOberservable = retrofit.create(ApiService::class.java).getChannel()
            channelOberservable.enqueue(object : Callback<List<Channel>?> {
                override fun onFailure(call: Call<List<Channel>?>, t: Throwable) {
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<List<Channel>?>, response: Response<List<Channel>?>) {
                    MovieTubeUtils.channels = response.body()!!
                    movieObservable.enqueue(object : Callback<List<Movie>?> {
                        override fun onFailure(call: Call<List<Movie>?>, t: Throwable) {
                            listener?.onError(t.localizedMessage)
                        }

                        override fun onResponse(call: Call<List<Movie>?>, response: Response<List<Movie>?>) {
                            listener?.onSuccess(response.body()!!)
                        }

                    })
                }
            })

        } else {
            this.channelListener?.onSuccess(MovieTubeUtils.channels)
            movieObservable.enqueue(object : Callback<List<Movie>?> {
                override fun onFailure(call: Call<List<Movie>?>, t: Throwable) {
                    listener?.onError(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<Movie>?>, response: Response<List<Movie>?>) {
                    listener?.onSuccess(response.body()!!)
                }

            })
        }


    }

    override fun putView(movie: Movie) {
        //  Toast.makeText(context, "PutView Called", Toast.LENGTH_SHORT).show()

        var putViewTsk = retrofit.create(ApiService::class.java).putView("application/json", movie)
        putViewTsk.enqueue(object : Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                //  Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                // Toast.makeText(context, "Response Code : " + response.Body(), Toast.LENGTH_SHORT).show()

            }

        })
    }

    override fun getTrending(page: Int, listener: Listener<List<Movie>>) {

        var trendingObservable = retrofit.create(ApiService::class.java).getTrending()
        if (MovieTubeUtils.trending.isEmpty()) {
            trendingObservable.enqueue(object : Callback<List<Movie>?> {
                override fun onFailure(call: Call<List<Movie>?>, t: Throwable) {
                    listener.onError(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<Movie>?>, response: Response<List<Movie>?>) {
                    MovieTubeUtils.trending = response.body()!!
                    listener.onSuccess(response.body()!!)
                }

            })
        } else {
            listener.onSuccess(MovieTubeUtils.trending)
        }


    }


    override fun getSuggestion(channel: Int, page: Int, listener: Listener<List<Movie>>) {
        var suggestionObservable = retrofit.create(ApiService::class.java).getSuggestion(channel, page)

        suggestionObservable.enqueue(object : Callback<List<Movie>?> {
            override fun onFailure(call: Call<List<Movie>?>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Movie>?>, response: Response<List<Movie>?>) {
                response.body()?.let {
                    listener.onSuccess(it)
                }
            }
        })


    }

    override fun getLiveSuggestion(page: Int, listener: Listener<List<LiveTv>>) {
        getLiveTv(page, listener)
    }


}