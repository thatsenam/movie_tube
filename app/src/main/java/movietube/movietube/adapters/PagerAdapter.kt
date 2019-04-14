package movietube.movietube.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import movietube.movietube.pages.*

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 5
    }

    override fun getItem(p0: Int): Fragment {
        var page = Fragment()
        when (p0) {
            0 -> page = MoviePage()
            1 -> page = TredingPage()
            2 -> page = UploadPage()
            3 -> page = StackOverflow()
            4 -> page = LibraryPage()
        }
        return page
    }

}