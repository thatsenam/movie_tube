package movietube.movietube.base

import android.content.Context
import android.support.annotation.StringRes

interface BaseView {

    fun getContext(): Context

    fun showError(error: String?)

    fun showError(@StringRes stringResId: Int)

    fun showMessage(@StringRes srtResId: Int)

    fun showMessage(message: String)
}