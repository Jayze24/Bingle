package space.jay.bingle.modules.loading

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import space.jay.bingle.R

class ActivityLoading : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        ManagerLoading.setLoadingActivity(this)
    }

    override fun onResume() {
        super.onResume()
        setContentView(ViewLoading(this, null))
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        ManagerLoading.setLoadingActivity(null)
    }

}
