package space.jay.bingle.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.databinding.ActivityLoginBinding
import space.jay.bingle.modules.ManagerVersion
import space.jay.bingle.modules.LoginGoogle
import space.jay.bingle.modules.dialog.ManagerDialog
import space.jay.bingle.ui.main.ActivityMain
import javax.inject.Inject

class ActivityLogin : AppCompatActivity() {

    @Inject
    lateinit var mManagerVersion: ManagerVersion
    @Inject
    lateinit var mLoginGoogle: LoginGoogle
    private lateinit var mBinding: ActivityLoginBinding
    private val mViewModelLogin by lazy { ViewModelProvider.AndroidViewModelFactory(application).create(
        ViewModelLogin::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.loginGoogle = mLoginGoogle
        mViewModelLogin.setObserve(this, mBinding, mManagerVersion)
    }

    override fun onStart() {
        super.onStart()
        if (mLoginGoogle.getUser()) {
            mViewModelLogin.sendVersionServerToLiveData(mBinding, mManagerVersion)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.RequestCode.GOOGLE_SIGN_IN -> mLoginGoogle.result(requestCode, data, mViewModelLogin)
        }
    }
}
