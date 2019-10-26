package movietube.movietube.interfaces

import com.google.gson.JsonElement
import movietube.movietube.pojo.*
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @GET("qa")
    fun postList(): Call<List<Post>>

    @GET("post")
    fun getMovies(@Query("page") page: Int): Call<List<Movie>?>

    @GET("channel")
    fun getChannel(): Call<List<Channel>?>


    @Headers("Content-Type: application/json")
    @PATCH("post/views")
    fun putView(@Header("Content-Type") type: String, @Body movie: Movie): Call<JsonElement>

    @GET("live")
    fun getLives(@Query("page") page: Int = 0): Call<List<LiveTv>?>

    @GET("post/trend")
    fun getTrending(): Call<List<Movie>?>

    @GET("qa/comments")
    fun getComments(@Query("p_id") id: Int, @Query("Type") type: String): Call<List<Comment>?>

    @GET("post/suggestion")
    fun getSuggestion(@Query("channel") channel: Int, @Query("page") page: Int): Call<List<Movie>?>


}