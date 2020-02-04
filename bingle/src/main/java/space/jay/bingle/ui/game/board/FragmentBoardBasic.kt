package space.jay.bingle.ui.game.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import space.jay.bingle.R
import space.jay.bingle.databinding.FragmentBoardBasicBinding

class FragmentBoardBasic : FragmentBoard() {

    companion object {
        fun newInstance() = FragmentBoardBasic()
    }

    private lateinit var viewModel: ViewModelBoard
    private lateinit var mBinding: FragmentBoardBasicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_basic, container, false)
        setViewInit()
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ViewModelBoard::class.java)
        // TODO: Use the ViewModel
    }

    private fun setViewInit() {
        val listOfTileView = arrayOf(
            mBinding.imageButtonBasic19,
            mBinding.imageButtonBasic21,
            mBinding.imageButtonBasic22,
            mBinding.imageButtonBasic23,
            mBinding.imageButtonBasic24,
            mBinding.imageButtonBasic39,
            mBinding.imageButtonBasic41,
            mBinding.imageButtonBasic42,
            mBinding.imageButtonBasic43,
            mBinding.imageButtonBasic44,
            mBinding.imageButtonBasic51,
            mBinding.imageButtonBasic52,
            mBinding.imageButtonBasic53,
            mBinding.imageButtonBasic54,
            mBinding.imageButtonBasic55,
            mBinding.imageButtonBasic61,
            mBinding.imageButtonBasic62,
            mBinding.imageButtonBasic63,
            mBinding.imageButtonBasic64,
            mBinding.imageButtonBasic65,
            mBinding.imageButtonBasic71,
            mBinding.imageButtonBasic72,
            mBinding.imageButtonBasic73,
            mBinding.imageButtonBasic74,
            mBinding.imageButtonBasic75,
            mBinding.imageButtonBasic81,
            mBinding.imageButtonBasic82,
            mBinding.imageButtonBasic83,
            mBinding.imageButtonBasic84,
            //버켓의 시작 뷰 뒷자리 0
            mBinding.viewBasicBucket110,
            mBinding.viewBasicBucket120,
            mBinding.viewBasicBucket210,
            mBinding.viewBasicBucket220,
            mBinding.viewBasicBucket310,
            mBinding.viewBasicBucket320,
            mBinding.viewBasicBucket410,
            mBinding.viewBasicBucket420,
            //버켓의 종료 뷰 뒷자리 9
            mBinding.viewBasicBucket119,
            mBinding.viewBasicBucket129,
            mBinding.viewBasicBucket219,
            mBinding.viewBasicBucket229,
            mBinding.viewBasicBucket319,
            mBinding.viewBasicBucket329,
            mBinding.viewBasicBucket419,
            mBinding.viewBasicBucket429,
            //버켓의 종료 뷰의 클릭 받는 뷰
            mBinding.viewBasicBucketEndZone)

        val listOfToken = arrayOf(
            mBinding.imageButtonBasicToken11,
            mBinding.imageButtonBasicToken12
        )

        setInit(mBinding.constraintLayoutBasicContainer, listOfTileView, listOfToken)
    }

}
