package movietube.movietube.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import movietube.movietube.pages.MoviePage
import movietube.movietube.pages.*

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 5
    }

    override fun getItem(p0: Int): Fragment {
        var page = Fragment()
        when (p0) {
            0 -> page = MoviePage()
            1 -> page = TrendingPage()
            2 -> page = UploadPage()
            3 -> page = StackOverflow()
            4 -> page = LibraryPage()
        }
        return page
    }

}