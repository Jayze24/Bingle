package space.jay.bingle.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import space.jay.bingle.R
import space.jay.bingle.modules.loading.ManagerLoading

class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ManagerLoading.show(this)
    }
}
