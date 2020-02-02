package space.jay.bingle.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.data.TypeBoard
import space.jay.bingle.databinding.ActivityMainBinding
import space.jay.bingle.modules.loading.ManagerLoading
import space.jay.bingle.ui.game.ActivityGame

class ActivityMain : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //todo 임시 클릭리스너
        mBinding.buttonMainGame.setOnClickListener {
            ManagerLoading.show(this)
            val intent = Intent(this, ActivityGame::class.java).apply {
                putExtra(Constants.Intent.EXTRA_TYPE_BOARD, TypeBoard.BASIC)
            }
            startActivity(intent)
        }
    }
}
