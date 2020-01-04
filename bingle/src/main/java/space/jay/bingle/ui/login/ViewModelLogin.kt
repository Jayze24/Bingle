package space.jay.bingle.ui.login

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.data.LiveDataObject
import space.jay.bingle.databinding.ActivityLoginBinding
import space.jay.bingle.modules.ManagerVersion
import space.jay.bingle.modules.RetrofitService
import space.jay.bingle.modules.permissions.Permissions
import space.jay.bingle.modules.dialog.ManagerDialog
import space.jay.bingle.ui.main.ActivityMain
import java.security.Permission

class ViewModelLogin(application: Application) : AndroidViewModel(application) {

    private var mLoginLiveData: MutableLiveData<LiveDataObject> = MutableLiveData()
    private val mSharedPreferences by lazy { application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE) }

    fun getMessageObservable() = mLoginLiveData

    fun sendVersionServerToLiveData(binding: ActivityLoginBinding, managerVersion: ManagerVersion) {
        binding.isSigned = true
        managerVersion.sendVersionServerToLiveData(mLoginLiveData)
    }

    fun setObserve(activity: ActivityLogin, binding: ActivityLoginBinding, managerVersion: ManagerVersion) {
        mLoginLiveData.observe(activity, Observer { handleMessage(activity, it, binding, managerVersion) })
    }

    private fun handleMessage(activity: ActivityLogin, message: LiveDataObject, binding: ActivityLoginBinding, managerVersion: ManagerVersion) {
        when (message.result) {
            Constants.Server.CONNECTION_SUCCESS -> {
                when (message.event) {
                    //1. 인증 성공 후 버전 정보 요청
                    Constants.Event.LOGIN_GOOGLE -> sendVersionServerToLiveData(binding, managerVersion)

                    //2. 서버에서 버전 정보를 가져와 이곳을 호출하면 버전 체크를 함
                    Constants.Event.VERSION -> {
                        when (message.message) {
                            managerVersion.getVersionFromServer().alert1 -> {
                                //필수 업데이트
                                ManagerDialog().show(
                                    activity,
                                    null,
                                    getApplication<Application>().getString(R.string.update_required),
                                    message.message,
                                    getApplication<Application>().getString(R.string.update),
                                    {
                                        //업데이트 버튼 -> 앱 종료 및 구글플레이로 연결
                                        goGooglePlay(activity)
                                    },
                                    null,
                                    "required update"
                                )
                            }
                            managerVersion.getVersionFromServer().alert2 -> {
                                //일반 업데이트
                                val now = System.currentTimeMillis()
                                if (mSharedPreferences.getLong(Constants.Preferences.UPDATE_SCHEDULE, 0) > now) {
                                    ManagerDialog()
                                        .show(
                                        activity,
                                        null,
                                        getApplication<Application>().getString(R.string.update),
                                        message.message,
                                        getApplication<Application>().getString(R.string.later),
                                        {
                                            // 나중에 버튼 -> 메인 액티비티 호출 3일 뒤에 다시 알리기
                                            mSharedPreferences.edit()
                                                .putLong(Constants.Preferences.UPDATE_SCHEDULE, now + 259200000)
                                                .apply()
                                            goMain(activity)
                                        },
                                        getApplication<Application>().getString(R.string.update),
                                        {
                                            //업데이트 버튼 -> 앱 종료 및 구글플레이 연결
                                            goGooglePlay(activity)
                                        },
                                        null,
                                        "update"
                                    )
                                } else {
                                    goMain(activity)
                                }
                            }
                            Constants.Init.STRING -> {
                                goMain(activity)
                            }
                        }
                    }
                }
            }
            Constants.Server.CONNECTION_FAILURE -> {
                when (message.event) {
                    Constants.Event.LOGIN_GOOGLE -> {
                        //activity 종료 안하는 다이얼 로그
                        ManagerDialog().show(
                            activity,
                            null,
                            null,
                            getApplication<Application>().getString(R.string.auth_failure),
                            getApplication<Application>().getString(R.string.ok),
                            {},
                            null,
                            "login google"
                        )
                    }
                    else -> {
                        //activity 종료 하는 다이얼 로그
                        ManagerDialog().show(
                            activity,
                            null,
                            null,
                            message.message,
                            getApplication<Application>().getString(R.string.ok),
                            {},
                            DialogInterface.OnDismissListener {
                                activity.finish()
                            },
                            "server error"
                        )
                    }
                }
            }
        }
    }

    private fun goMain(activity : Activity) {
        //메인 엑티비티 이동
        RetrofitService.isURLChanged = true
        activity.startActivity(Intent(activity, ActivityMain::class.java))
    }

    private fun goGooglePlay(activity : Activity) {
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${activity.packageName}")
                )
            )
        } catch (e: Exception) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}")
                )
            )
        }
        activity.finish()
    }
}