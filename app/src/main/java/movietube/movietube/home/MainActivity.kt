package movietube.movietube.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_controller_layout.*
import movietube.movietube.Listeners
import movietube.movietube.MovieTubeUtils
import movietube.movietube.R
import movietube.movietube.adapters.LiveSuggestAdapter
import movietube.movietube.adapters.MovieCommentAdapter
import movietube.movietube.adapters.PagerAdapter
import movietube.movietube.adapters.SuggestedAdapter
import movietube.movietube.base.BaseActivity
import movietube.movietube.listeners.Listener
import movietube.movietube.listeners.OnFragmentInteraction
import movietube.movietube.model.Model
import movietube.movietube.pages.EndlessRecyclerViewScrollListener
import movietube.movietube.pojo.*
import movietube.movietube.provider.VeryStreamProvider
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.random.*


/*public val MainActivity.videoPlayer: GiraffePlayer?
    get() {
        return mediaView.setVideoPath(MainActivity.TEST_URL).player
 }*/

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentInteraction,
    ViewPager.OnPageChangeListener, MainContact.View, Player.EventListener, View.OnClickListener {

    companion object {
        var TEST_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4"
        var TEST_UR2 = "https://vs081.vshare.eu:182/d/heemeokh7hnroluvqobryzfabohxn36kfwwrmmv3r37eregyoxgukklu/vid.mp4"
        var StreamUrl = "https://openload.co/stream/-aIj_mXj4f8~1555006293~103.114.0.0~ueXsvYCP"
        var screenNormal = 1
        var screenFull = 2
        var screenMini = 3
        var currentScreenState = -1
        var PLAYER_ERROR = false
        var full = false
        lateinit var activity: Activity
        var VIDEO_STATE: VideoState? = null
    }

    val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
        get() {
            val bottomSheet = BottomSheetBehavior.from(bottomSheetView)
            return bottomSheet
        }
    private val tag: String = "MainActivity"
    lateinit var model: MainContact.Model
    lateinit var mPresenter: MainContact.Presenter
    lateinit var player: SimpleExoPlayer
    var wasPlaying: Boolean = false
    var handler: Handler = Handler()
    lateinit var fullScreenDialoug: Dialog
    var playingMovie: Movie

    init {
        playingMovie = Movie()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = Model(this)
        mPresenter = MainPresenter(this, model)
        mPresenter.enqueue()
    }

    override fun defaultConfigure() {
        fullScreenDialoug = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialoug.setCancelable(false)
        homeViewPager.adapter = PagerAdapter(supportFragmentManager)
        homeViewPager.addOnPageChangeListener(this)
        bottomMenu.setOnNavigationItemSelectedListener(this)
        /*     backward.setOnClickListener(object : DoubleClickListener() {
                 override fun onSingleClick(v: View) {
                     mediaView.performClick()
                 }

                 override fun onDoubleClick(v: View) {
                     player.seekTo(player.currentPosition - 10000)
                 }

             })
             forward.setOnClickListener(object : DoubleClickListener() {
                 override fun onSingleClick(v: View) {
                     mediaView.performClick()
                 }

                 override fun onDoubleClick(v: View) {
                     player.seekTo(player.currentPosition + 10000)
                 }

             })*/
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        exo_progress.setPadding(0, 0, 0, 0)
        suggestedMovieRV.isNestedScrollingEnabled = true
        nestedScrollView.isNestedScrollingEnabled = true
        exo_fullscreen.setOnClickListener {
            if (currentScreenState == screenNormal) {
                fullScreen()
            } else {
                normalScreen()
            }

        }
        exo_retry.setOnClickListener {
            player.retry()
            updatePlaybackButton()
            it.visibility = View.GONE
        }
        exo_minimize.setOnClickListener {
            if (currentScreenState == screenFull) {
                normalScreen()
            } else {
                miniScreen()
            }
        }
        exo_more.setOnClickListener {
            MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(R.array.MoreItem) { dialog, index, text ->
                    fullScreenDialoug.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    hide()
                }

            }
        }
        ViewCompat.setElevation(app_bar, resources.getDimension(R.dimen.toolbar_elevation))
        ViewCompat.setElevation(bottomSheetContainer, resources.getDimension(R.dimen.toolbar_elevation))
        activity = this


    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bottomDetailContainer -> {
                movieDetailsContainer.visibility =
                    if (movieDetailsContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            else -> {

            }
        }
    }

    fun initCommentItem(commentType: String, id: Int) {
        movie_comment_rv.layoutManager = LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
        var adapter =
            MovieCommentAdapter(getContext(), mutableListOf(), object : Listeners.OnCommentItemClickListener {
                override fun onCommentClick(item: View, position: Int, post: Post) {

                }

                override fun onReplyClick(item: View, position: Int, post: Post) {

                }
            }, model)
        movie_comment_rv.adapter =
            adapter

        model.getComments(0, commentType, id, listener = object : Listener<List<Comment>>() {

            override fun onSuccess(response: List<Comment>) {
                if (response != null && response.size != 0) {
                    adapter.clearAll()
                    adapter.addItem(response.toMutableList())
                }
            }

            override fun onError(error: String) {
                toast(error)
            }

        })


    }


    override fun playVideo(movie: Movie, link: Link) {
        if (playingMovie.id == movie.id) {
            return;
        }
        PLAYER_ERROR = false
        exo_error.visibility = View.GONE
        exo_retry.visibility = View.GONE

        var channel = getChannel(movie.ChannelID)
        VIDEO_STATE = VideoState.MOVIE
        playingMovie = movie;
        log(Thread.currentThread().name)
        /*WebFetcher.getInstance(this, browser as WebView, object : Listener<String>() {
            override fun onError(error: String) {
                toast(error)
            }

            override fun onSuccess(response: String) {
                log(Thread.currentThread().name)
                try {
                    runOnUiThread {
                        log(Thread.currentThread().name)
                        val vi = VideoInfo()
                        vi.uri = Uri.parse(response)
                        vi.Title = "Test Title"
                        vi.aspectRatio = VideoInfo.AR_ASPECT_FIT_PARENT
                        vi.bgColor = Color.BLACK
                        mediaView.videoInfo(vi)
                        mediaView.player.start()
                    }


                } catch (exeption: Exception) {
                    toast(exeption.localizedMessage)
                    log(exeption.printStackTrace().toString())
                }


            }
        }, "HL6L3Drj79o").fetchUrl()*/
        Log.w("info", movie.toString())

        /*Collapsed Container*/

        publishDateTV.text = "Published on " + movie.PostDate.toString()
        movieDescriptionTV.text = movie.Plot.toString()


        /*Comment Rv*/
        initCommentItem("movie", movie.id)
        stream_title.text = movie.Title
        movieTitleTV.text = movie.Title
        exo_title.text = movie.Title
        stream_channel.text = channel?.Title
        movie_channel_title.text = channel?.Title
        movie_channel_subscriber.text = MovieTubeUtils.format(
            if (channel?.Subscriber?.toLong() == null) {
                0
            } else {
                channel.Subscriber?.toLong()!!
            }
        ) + " Subscribers"
        movie_likes.text = "${MovieTubeUtils.format(movie.Likes?.toLong()!!)}"
        movie_dislikes.text = "${MovieTubeUtils.format(movie.Dislikes?.toLong()!!)}"
        movie_views.text = "${MovieTubeUtils.format(movie.Views?.toLong()!!)} views"
        Glide.with(getContext()).load(channel?.Poster).into(movie_channel_thumb)
        movie.Views = movie.Views!! + 1;
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (link.LinkProvider == LinkProvider.VERY_STREAM.provider) {
            VeryStreamProvider.getInstance(this).fetch(link.Link, "videolink", object : Listener<String>() {
                override fun onSuccess(response: String) {
                    playVideo(Uri.parse(response))
                }

                override fun onError(error: String) {
                    toast(error)
                }

            })
        } else if (link.LinkProvider == LinkProvider.DIRECT.provider) {
            playVideo(Uri.parse(link.Link))
        } else {
            playVideo(Uri.parse("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"))
            Toast.makeText(applicationContext, link.LinkProvider + " provider not added yet", Toast.LENGTH_SHORT)
                .show()
        }
        model.putView(movie)
        model.getSuggestion(playingMovie.ChannelID!!, 0, object : Listener<List<Movie>>() {
            override fun onSuccess(response: List<Movie>) {
                suggestedMovie(response)
            }

            override fun onError(error: String) {
                toast(error)
            }
        })
        nestedScrollView.scrollTo(0, 0)

    }

    override fun playLive(liveTv: LiveTv, link: Link) {
        if (playingMovie.id == liveTv.id) {
            return;
        }
        var channel = null
        VIDEO_STATE = VideoState.LIVE
        log(Thread.currentThread().name)
        Log.w("info", liveTv.toString())
        stream_title.text = liveTv.Title
        movieTitleTV.text = liveTv.Title
        exo_title.text = liveTv.Title

        /*Comment */
        initCommentItem("live", liveTv.id)


        stream_channel.text = "live"
        movie_channel_title.text = "movietube"
        movie_channel_subscriber.text = MovieTubeUtils.format(Random(2).nextInt(5000).toLong()) + " Subscribers"
        movie_likes.text = "${MovieTubeUtils.format(liveTv.Likes?.toLong()!!)}"
        movie_dislikes.text = "${MovieTubeUtils.format(liveTv.Dislikes?.toLong()!!)}"
        movie_views.text = "${MovieTubeUtils.format(liveTv.Views?.toLong()!!)} views"
        Glide.with(getContext()).load(ContextCompat.getDrawable(applicationContext, R.drawable.ic_profile))
            .into(movie_channel_thumb)
        liveTv.Views = liveTv.Views!! + 1;
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (link.LinkProvider == LinkProvider.VERY_STREAM.provider) {
            VeryStreamProvider.getInstance(this).fetch(link.Link, "videolink", object : Listener<String>() {
                override fun onSuccess(response: String) {
                    playVideo(Uri.parse(response))
                }

                override fun onError(error: String) {
                    toast(error)
                }

            })
        } else if (link.LinkProvider == LinkProvider.DIRECT.provider) {
            playVideo(Uri.parse(link.Link), hls = true)
        } else {
            playVideo(Uri.parse("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"))
            Toast.makeText(applicationContext, link.LinkProvider + " provider not added yet", Toast.LENGTH_SHORT)
                .show()
        }
        suggestLive(liveTv)
        nestedScrollView.scrollTo(0, 0)
    }

    fun toast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun suggestedMovie(movies: List<Movie>) {
        suggestedMovieRV.visibility = View.VISIBLE
        suggestedLiveRV.visibility = View.GONE
        var linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        suggestedMovieRV.layoutManager = linearLayoutManager
        var adapter = SuggestedAdapter(movies.toMutableList(), object : Listeners.MovieClickListener {
            override fun onMovieClick(item: View, position: Int, targetMovie: Movie) {
                playVideo(targetMovie, targetMovie.Links?.get(0)!!)
            }
        })
        suggestedMovieRV.adapter = adapter
        var suggestionScroller = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                load_more_suggestion.visibility = View.VISIBLE
                load_more_suggestion.setOnClickListener {
                    load_more_suggestion.isEnabled = false
                    playingMovie.ChannelID?.let {
                        mPresenter.loadMoreSuggestion(it, page, object : Listener<List<Movie>>() {
                            override fun onSuccess(response: List<Movie>) {
                                adapter.addMovie(response.toMutableList())
                                load_more_suggestion.visibility = View.VISIBLE
                                load_more_suggestion.isEnabled = true

                            }

                            override fun onError(error: String) {
                                toast(error)
                            }

                        })
                    }
                }

            }
        }
        suggestedMovieRV.addOnScrollListener(suggestionScroller)


    }

    fun suggestLive(liveTv: LiveTv) {
        suggestedLiveRV.visibility = View.VISIBLE
        suggestedMovieRV.visibility = View.GONE
        var linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        suggestedLiveRV.layoutManager = linearLayoutManager
        var adapter = LiveSuggestAdapter(mutableListOf(), object : Listeners.LiveListener {
            override fun onLiveClick(item: View, position: Int, live: LiveTv) {
                playLive(live, live.Links?.get(0)!!)
            }
        })
        model.getLiveSuggestion(0, object : Listener<List<LiveTv>>() {
            override fun onSuccess(response: List<LiveTv>) {
                adapter.addLive(response.toMutableList())
            }

            override fun onError(error: String) {

            }

        })

        suggestedLiveRV.adapter = adapter
        var suggestionScroller = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {

                load_more_suggestion.visibility = View.VISIBLE
                load_more_suggestion.setOnClickListener {
                    load_more_suggestion.isEnabled = false
                    playingMovie.ChannelID?.let {
                        model.getLiveSuggestion(page, object : Listener<List<LiveTv>>() {

                            override fun onSuccess(response: List<LiveTv>) {
                                load_more_suggestion.visibility = View.VISIBLE
                                load_more_suggestion.isEnabled = true

                            }

                            override fun onError(error: String) {

                            }

                        })
                    }

                }

            }
        }
        suggestedLiveRV.addOnScrollListener(suggestionScroller)
    }

    fun getChannel(i: Int?): Channel? {
        for (channel in MovieTubeUtils.channels) {
            if (channel.id == i) {
                return channel
            }
        }

        return null
    }

    override fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)
        player.playWhenReady = true
        mediaView.player = player
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        mediaView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        player.addListener(this)
        mediaView.setControllerVisibilityListener {
            if (it == View.VISIBLE) {
                if (PLAYER_ERROR) {
                    Handler().postDelayed({
                        exo_play.visibility = View.GONE
                    }, 100)
                }

            }
        }
        PLAYER_ERROR = false
        exo_error.visibility = View.GONE
        exo_retry.visibility = View.GONE
    }

    fun playVideo(uri: Uri, hls: Boolean = false) {
        PLAYER_ERROR = false
        exo_error.visibility = View.GONE
        exo_retry.visibility = View.GONE
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "applicationName"))
        if (!hls) {
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            player.prepare(videoSource, false, true)
        } else {
            val videoSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            player.prepare(videoSource, false, true)
        }

        player.playWhenReady = true

    }


    override fun onFragmentItemClick(view: View) {

    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        bottomMenu.translationY = 0.toFloat()
        when (p0.itemId) {
            R.id.home -> homeViewPager.currentItem = 0
            R.id.trending -> homeViewPager.currentItem = 1
            R.id.upload -> homeViewPager.currentItem = 2
            R.id.stackoverflow -> homeViewPager.currentItem = 3
            R.id.library -> homeViewPager.currentItem = 4
        }
        return true
    }


    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(p0: Int) {
        when (p0) {
            0 -> bottomMenu.selectedItemId = R.id.home
            1 -> bottomMenu.selectedItemId = R.id.trending
            2 -> bottomMenu.selectedItemId = R.id.upload
            3 -> bottomMenu.selectedItemId = R.id.stackoverflow
            4 -> bottomMenu.selectedItemId = R.id.library

        }
    }


    override fun configureBottomSheet() {
        currentScreenState = screenMini
        val bottomSheet = bottomSheetBehavior
        Log.w(tag, "Foo " + bottomSheet.isHideable.toString())
        bottomSheet.isHideable = true
        val layoutParams1 = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
        var margin1 = MovieTubeUtils.convertDpToPixel(6.toFloat(), applicationContext).toInt()
        var bottomMargin1 = (MovieTubeUtils.convertDpToPixel(63.toFloat(), applicationContext)).toInt()
        layoutParams1.setMargins(margin1, margin1, margin1, bottomMargin1)
        bottomSheet.peekHeight = 120
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                if (p1 < 0) {
                    if (bottomSheet.state != BottomSheetBehavior.STATE_COLLAPSED) {
                        //  bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return
                }
                var tracker = 1.0
                tracker -= p1
                var layoutParams = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
                var margin = (tracker * 6).toInt()
                margin = MovieTubeUtils.convertDpToPixel(margin.toFloat(), applicationContext).toInt()
                var bottomMargin =
                    (MovieTubeUtils.convertDpToPixel((tracker * 63).toFloat(), applicationContext)).toInt()
                layoutParams.setMargins(margin, margin, margin, bottomMargin)
                bottomSheetContainer.layoutParams = layoutParams
                var trans = bottomMenu.height * p1
                bottomMenu.translationY = trans
                bottomSheet.peekHeight = 120
                Log.w(tag, "Slide : $p1 \t \t | view : Y ${p0.y} ")
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(p0: View, p1: Int) {

                when (p1) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        exo_error.visibility = View.GONE
                        var params = mediaView.layoutParams
                        var layoutParams = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
                        var margin = 6
                        margin = MovieTubeUtils.convertDpToPixel(margin.toFloat(), applicationContext).toInt()
                        var bottomMargin = (MovieTubeUtils.convertDpToPixel(63.toFloat(), applicationContext)).toInt()
                        layoutParams.setMargins(margin, margin, margin, bottomMargin)
                        bottomSheetContainer.layoutParams = layoutParams
                        ValueAnimator.ofInt(params.width, 250).apply {
                            duration = 400
                            start()
                            addUpdateListener {
                                val value = it.animatedValue as Int
                                params.width = value
                                mediaView.layoutParams = params
                            }

                        }

                        ValueAnimator.ofInt(params.height, 120).apply {
                            duration = 400
                            start()
                            addUpdateListener {
                                val value = it.animatedValue as Int
                                params.height = value
                                mediaView.layoutParams = params
                            }
                        }

                        bottomController.visibility = View.VISIBLE
                        mediaView.useController = false
                        currentScreenState = screenMini
                        //  ObjectAnimator.ofFloat(bottomMenu, "translationY", 0.0f)
                        if (bottomMenu.translationY != 0.0f) {
                            bottomMenu.translationY = 0.0f
                        }

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        if (PLAYER_ERROR) {
                            exo_error.visibility = View.VISIBLE
                        }

                        mediaView.useController = true
                        var params = mediaView.layoutParams
                        /*    params.height = 400
                            params.width = ViewPager.LayoutParams.MATCH_PARENT
                            mediaView.layoutParams = params*/

                        bottomController.visibility = View.INVISIBLE
                        /*    bottomMenu.translationY = 0.toFloat()
                            var layoutParams = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
                            var margin = 6
                            margin = MovieTubeUtils.convertDpToPixel(margin.toFloat(), applicationContext).toInt()
                            var bottomMargin = (MovieTubeUtils.convertDpToPixel(63.toFloat(), applicationContext)).toInt()
                            layoutParams.setMargins(margin, margin, margin, bottomMargin)
                            bottomSheetContainer.layoutParams = layoutParams*/

                        ValueAnimator.ofInt(params.width, ViewPager.LayoutParams.MATCH_PARENT).apply {
                            duration = 200
                            start()
                            addUpdateListener {
                                val value = it.animatedValue as Int
                                params.width = value
                                mediaView.layoutParams = params
                            }
                        }

                        ValueAnimator.ofInt(params.height, 400).apply {
                            duration = 500
                            start()
                            addUpdateListener {
                                val value = it.animatedValue as Int
                                params.height = value
                                mediaView.layoutParams = params
                            }
                        }
                        currentScreenState = screenNormal


                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        /*  params.height = 124
                            params.width = 250
                            mediaView.layoutParams = params
                            bottomController.visibility = View.VISIBLE

                         */
                        mediaView.useController = false

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        player.stop()

                    }
                }


            }
        })
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetView.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        pausePlayBTN.setOnClickListener {
            if (isPlaying()) {
                mediaView.player.playWhenReady = false
                pausePlayBTN.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_black_24dp))
            } else {
                player.playWhenReady = true
                pausePlayBTN.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_black_24dp))
            }
        }
        closeBTN.setOnClickListener {
            mediaView.useController = false
            player.stop()
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun isPlaying(): Boolean {
        return player.playbackState == Player.STATE_READY && player.playWhenReady
    }

    private fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun Context.log(message: CharSequence) = Log.e(tag, message.toString())
    fun Int.lol(i: Int): Int {
        return i + i
    }

    private fun hlsMediaSource(uri: Uri, context: Context): MediaSource {


        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "oxoo"), bandwidthMeter
        )


        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    enum class LinkProvider(var provider: String) {
        VERY_STREAM("very_stream"),
        OPENLOAD("openload"),
        DIRECT("direct")
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }


    override fun onPause() {
        super.onPause()
        if (isPlaying()) {
            wasPlaying = true
            player.playWhenReady = false
        } else {
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (wasPlaying) {
            player.playWhenReady = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {

        // StartAppAd.onBackPressed(this)
        log(currentScreenState.toString())
        if (currentScreenState == screenFull) {
            normalScreen()
        } else if (currentScreenState == screenNormal) {
            miniScreen()
        } else {
            super.onBackPressed()
        }


    }

    override fun fullScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (mediaView.parent as ViewGroup).removeView(mediaView)

        fullScreenDialoug.setCanceledOnTouchOutside(false)

        fullScreenDialoug.window
            .setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        fullScreenDialoug.window
            .addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        fullScreenDialoug.window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )

        fullScreenDialoug.addContentView(
            mediaView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )


        exo_title.visibility = View.VISIBLE
        exo_fullscreen.background = (ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit_black_24dp))
        currentScreenState = screenFull
        fullScreenDialoug.show()
        fullScreenDialoug.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

    }

    override fun normalScreen() {
        toast("request for normal screen")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mediaView.parent as ViewGroup).removeView(mediaView)
        exo_parent.addView(
            mediaView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        )
        // fullScreenDialoug.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        fullScreenDialoug.dismiss()
        currentScreenState = screenNormal
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        exo_fullscreen.background = (ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_white_24dp))
        exo_title.visibility = View.INVISIBLE

    }

    override fun miniScreen() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

    }


    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //  hideSystemUI()
        } else if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            /* app_bar.fitsSystemWindows = true
             toolbar.fitsSystemWindows = true */
            // showSystemUI()
            /*   var params = toolbar.layoutParams as AppBarLayout.LayoutParams
               params.scrollFlags = 0
               params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                       or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)*/

        }
    }


    fun onTimelineChanged(timeline: Timeline, manifest: Any) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }


    fun updatePlaybackButton() {
        when (player.playbackState) {
            PlaybackState.STATE_ERROR -> {
                Handler().postDelayed({
                    if (currentScreenState != screenMini) {
                        exo_error.visibility = View.VISIBLE
                    }
                    PLAYER_ERROR = true
                    exo_play.visibility = View.GONE
                    exo_pause.visibility = View.GONE
                    exo_retry.visibility = View.VISIBLE
                }, 100)

            }
            PlaybackState.STATE_PLAYING -> {
                /*  exo_error.visibility = View.GONE
                  PLAYER_ERROR = false
                  if (player.playWhenReady) {
                      exo_pause.visibility = View.VISIBLE
                      exo_retry.visibility = View.GONE
                  } else {
                      exo_play.visibility = View.VISIBLE
                      exo_retry.visibility = View.GONE
                  }*/
                Handler().postDelayed({
                    exo_error.visibility = View.GONE
                    if (player.playWhenReady) {
                        exo_pause!!.visibility = View.VISIBLE
                        exo_retry!!.visibility = View.GONE
                    } else {
                        exo_play!!.visibility = View.VISIBLE
                        exo_retry!!.visibility = View.GONE
                    }
                    PLAYER_ERROR = false
                }, 100)


            }
            PlaybackState.STATE_STOPPED -> {
                Handler().postDelayed({
                    exo_error.visibility = View.VISIBLE
                    PLAYER_ERROR = true
                    exo_play.visibility = View.GONE
                    exo_pause.visibility = View.GONE
                    exo_retry.visibility = View.VISIBLE
                }, 100)

            }

        }
    }


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        updatePlaybackButton()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        when (error.type) {
            ExoPlaybackException.TYPE_SOURCE ->
                playerError("TYPE_SOURCE: " + error.sourceException.message)

            ExoPlaybackException.TYPE_RENDERER ->
                playerError("TYPE_RENDERER: " + error.rendererException.message)

            ExoPlaybackException.TYPE_UNEXPECTED ->
                playerError("TYPE_UNEXPECTED: " + error.unexpectedException.message)

            ExoPlaybackException.TYPE_OUT_OF_MEMORY ->
                playerError("TYPE_OUT_OF_MEMORY: " + error.unexpectedException.message)

            ExoPlaybackException.TYPE_REMOTE ->
                playerError("TYPE_REMOTE: " + error.unexpectedException.message)
        }
    }


    private fun playerError(err: String) {

        exo_error.visibility = View.VISIBLE
        exo_error.text = err

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.contribute -> {
                if (full) {
                    showSystemUI()
                    full = false
                } else {
                    hideSystemUI()
                    full = true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onPositionDiscontinuity() {

    }


    abstract inner class DoubleClickListener : View.OnClickListener {

        internal var lastClickTime: Long = 0

        override fun onClick(v: View) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < 300) {
                onDoubleClick(v)
            } else {
                onSingleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onSingleClick(v: View)

        abstract fun onDoubleClick(v: View)

    }

    class CustomPolicy : DefaultLoadErrorHandlingPolicy() {

        override fun getRetryDelayMsFor(
            dataType: Int,
            loadDurationMs: Long,
            exception: IOException,
            errorCount: Int
        ): Long {
            return if (exception is FileNotFoundException)
                C.TIME_UNSET else super.getRetryDelayMsFor(
                dataType, loadDurationMs, exception, errorCount
            )
        }

    }

    enum class VideoState(val state: String) {
        MOVIE("movie"), LIVE("movie")
    }


}
