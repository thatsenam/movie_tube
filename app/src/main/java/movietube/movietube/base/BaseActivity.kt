package movietube.movietube.base


import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), BaseView {


    override fun getContext(): Context = this


    override fun showError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    override fun showError(stringResId: Int) {
        Toast.makeText(this, resources.getString(stringResId), Toast.LENGTH_SHORT).show()

    }

    override fun showMessage(srtResId: Int) {
        Toast.makeText(this, resources.getString(srtResId), Toast.LENGTH_SHORT).show()

    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}