package space.jay.bingle.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.data.TypeBoard
import space.jay.bingle.databinding.ActivityGameBinding
import space.jay.bingle.di.ModuleLogin
import space.jay.bingle.modules.loading.ManagerLoading
import java.lang.Exception

class ActivityGame : AppCompatActivity() {

    private lateinit var mBinding: ActivityGameBinding
    private val mViewModelGame by lazy { ViewModelProvider.AndroidViewModelFactory(application).create(ViewModelGame::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        mViewModelGame.setInit(supportFragmentManager, mBinding, intent.getSerializableExtra(Constants.Intent.EXTRA_TYPE_BOARD) as TypeBoard)

        ManagerLoading.dismiss()

        mBinding.tempButtonGameSend.setOnClickListener {
            try {
                mViewModelGame.setMovingNumber(mBinding.tempEditTextGameNumber.text.toString().toInt())
            } catch (e : Exception) {
                e.printStackTrace()
            }

        }
    }

    fun getViewModelGame() = mViewModelGame
}
