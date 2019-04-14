package movietube.movietube.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import movietube.movietube.Listener
import movietube.movietube.MovieTubeUtils
import movietube.movietube.R
import movietube.movietube.WebFetcher
import movietube.movietube.adapters.MovieClickListener
import movietube.movietube.adapters.PagerAdapter
import movietube.movietube.adapters.SuggestedAdapter
import movietube.movietube.base.BaseActivity
import movietube.movietube.listeners.OnFragmentInteraction
import movietube.movietube.pojo.Movie
import movietube.movietube.test.TestActivityMock
import tcking.github.com.giraffeplayer2.VideoInfo


/*public val MainActivity.videoPlayer: GiraffePlayer?
    get() {
        return mediaView.setVideoPath(MainActivity.TEST_URL).player
    }*/

class MainActivity() : BaseActivity<MainContact.View, MainContact.Presenter>(), BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentInteraction, ViewPager.OnPageChangeListener, MainContact.View {

    val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
        get() {
            val bottomSheet = BottomSheetBehavior.from(bottomSheetView)
            return bottomSheet
        }
    private val tag: String = "MainActivity"
    lateinit var model: MainContact.Model
    override lateinit var mPresenter: MainContact.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = Model(this)
        mPresenter = MainPresenter(model)
        mPresenter.attachView(this)
        mPresenter.enqueue()
    }


    override fun defaultConfigure() {
        homeViewPager.adapter = PagerAdapter(supportFragmentManager)
        homeViewPager.addOnPageChangeListener(this)
        bottomMenu.setOnNavigationItemSelectedListener(this)
        val bottomSheet = bottomSheetBehavior
        Log.w(tag, "Foo " + bottomSheet.isHideable.toString())
        bottomSheet.isHideable = true
        val vi = VideoInfo()
        vi.uri = Uri.parse(TEST_URL)
        vi.title = "Test Title"
        vi.aspectRatio = VideoInfo.AR_ASPECT_FIT_PARENT
        vi.bgColor = Color.BLACK
        mediaView.videoInfo(vi)
        mediaView.coverView.setImageDrawable(resources.getDrawable(R.drawable.abc_ab_share_pack_mtrl_alpha))
        mediaView.coverView.scaleType = ImageView.ScaleType.FIT_XY

        val layoutParams1 = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
        var margin1 = MovieTubeUtils.convertDpToPixel(6.toFloat(), applicationContext).toInt()
        var bottomMargin1 = (MovieTubeUtils.convertDpToPixel(63.toFloat(), applicationContext)).toInt()
        layoutParams1.setMargins(margin1, margin1, margin1, bottomMargin1)
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                Log.w(tag, "Slide : $p1 Peek : ${bottomSheet.peekHeight}")
                if (p1 < 0.0) return
                var tracker = 1.0
                tracker -= p1
                val layoutParams = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
                var margin = (tracker * 6).toInt()
                margin = MovieTubeUtils.convertDpToPixel(margin.toFloat(), applicationContext).toInt()
                var bottomMargin = (MovieTubeUtils.convertDpToPixel((tracker * 63).toFloat(), applicationContext)).toInt()
                layoutParams.setMargins(margin, margin, margin, bottomMargin)
                bottomSheetContainer.layoutParams = layoutParams
                var trans = 120 * p1
                bottomMenu.translationY = trans
            }

            override fun onStateChanged(p0: View, p1: Int) {
                var params = mediaView.layoutParams
                when (p1) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        params.height = 124
                        params.width = 250
                        mediaView.layoutParams = params
                        var controller = findViewById<RelativeLayout>(R.id.app_video_bottom_box)
                        controller.visibility = View.INVISIBLE
                        bottomController.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        params.height = 400
                        params.width = ViewPager.LayoutParams.MATCH_PARENT
                        mediaView.layoutParams = params
                        bottomController.visibility = View.GONE


                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        mediaView.player.release()
                    }
                }


            }
        })
        bottomSheet.peekHeight = 120
        val params = mediaView.layoutParams
        params.height = 124
        params.width = 250
        mediaView.layoutParams = params
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetView.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        pausePlayBTN.setOnClickListener {
            var controller = findViewById<RelativeLayout>(R.id.app_video_bottom_box)
            controller.visibility = View.INVISIBLE
            if (mediaView.player.isPlaying) {
                mediaView.player.pause()
                pausePlayBTN.setImageDrawable(resources.getDrawable(R.drawable.ic_play_black_24dp))
            } else {
                mediaView.player.start()
                pausePlayBTN.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_black_24dp))

            }

        }
        closeBTN.setOnClickListener {
            mediaView.player.release()

            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    override fun playVideo(url: Movie) {
        log(Thread.currentThread().name)
        mediaView.player.pause()
        mediaView.player.release()

        WebFetcher.getInstance(this, browser as WebView, object : Listener<String>() {
            override fun onError(error: String) {
                toast(error)
            }

            override fun onSuccess(response: String) {
                //  toast(response)
                // log(response)
                log(Thread.currentThread().name)
                try {
                    runOnUiThread {
                        log(Thread.currentThread().name)
                        val vi = VideoInfo()
                        vi.uri = Uri.parse(response)
                        vi.title = "Test Title"
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
        }, "e3GNrYtKq7c").fetchUrl()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED


    }


    override fun suggestedMovie(movies: List<Movie>) {
        suggestedMovieRV.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        suggestedMovieRV.adapter = SuggestedAdapter(movies, object : MovieClickListener {
            override fun onMovieClick(item: View, position: Int, targetMovie: Movie) {
                TEST_URL = TEST_UR2
                playVideo(targetMovie)
            }
        })
    }

    override fun hideBottomSheet() {

    }


    override fun initPlayer() {

    }


    override fun onFragmentItemClick(view: View) {

    }


    override fun onPause() {
        super.onPause()
        log("log test")

    }

    override fun onResume() {
        super.onResume()
        log("log test")

    }

    override fun onBackPressed() {
        startActivity(Intent(this, TestActivityMock::class.java))

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

    private fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun Context.log(message: CharSequence) = Log.e(tag, message.toString())
    fun Int.lol(i: Int): Int {
        return i + i
    }

    companion object {
        var TEST_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4"
        var TEST_UR2 = "https://vs081.vshare.eu:182/d/heemeokh7hnroluvqobryzfabohxn36kfwwrmmv3r37eregyoxgukklu/vid.mp4"
        var StreamUrl = "https://openload.co/stream/-aIj_mXj4f8~1555006293~103.114.0.0~ueXsvYCP"
    }
}
