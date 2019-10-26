package movietube.movietube

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.startapp.android.publish.adsCommon.StartAppAd
import com.startapp.android.publish.adsCommon.StartAppSDK
import movietube.movietube.home.MainActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StartAppSDK.init(this, resources.getString(R.string.app_id), true)
        StartAppAd.disableSplash()
        /*
        StartAppSDK.setUserConsent(
              this,
              "pas",
              System.currentTimeMillis(),
              true)
          */

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
