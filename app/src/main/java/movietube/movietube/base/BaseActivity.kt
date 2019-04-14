package movietube.movietube.base

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

abstract class BaseActivity<in V : BaseView, T : BasePresenter<V>> : AppCompatActivity(), BaseView {

    protected abstract var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        mPresenter.attachView(this as V)
    }

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
        mPresenter.detachView()
    }
}