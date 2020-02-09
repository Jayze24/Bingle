package space.jay.bingle.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.data.TypeBoard
import space.jay.bingle.databinding.ActivityGameBinding
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
                mViewModelGame.setMyTokenNumber(mBinding.tempEditTextGameNumber.text.toString().toInt())
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

        val arrayToken = arrayOf("11", "12", "21", "22", "31", "32", "41", "42")
        mBinding.tempSpinnerGameOtherToken.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayToken)
        val arrayTile = arrayOf("19", "21", "22", "23", "24", "39", "41", "42", "43", "44", "51", "52", "53", "54", "55", "61", "62", "63", "64", "65", "71", "72", "73", "74", "75", "81", "82", "83", "84")
        mBinding.tempSpinnerGameOtherTile.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayTile)
        mBinding.tempButtonGameOtherTokenSend.setOnClickListener {
            try {
                mViewModelGame.setOtherToken(mBinding.tempSpinnerGameOtherToken.selectedItem.toString(),
                    mBinding.tempSpinnerGameOtherTile.selectedItem.toString())
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getViewModelGame() = mViewModelGame
}
