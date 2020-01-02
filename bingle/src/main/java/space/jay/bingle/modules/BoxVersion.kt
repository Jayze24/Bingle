package space.jay.bingle.modules

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.data.LiveDataObject
import space.jay.bingle.data.Version

class BoxVersion(private val applicationContext: Context) {

    private var mVersionFromServer: Version = Version()
    private var mServerRetryCount = Constants.Init.INT

    fun sendVersionServerToLiveData(loginLiveData: MutableLiveData<LiveDataObject>) {
        RetrofitService.instance.getVersion().enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                if (mServerRetryCount < Constants.Server.SERVER_RETRY_MAX_COUNT) {
                    mServerRetryCount++

                    Log.d(this@BoxVersion.javaClass.simpleName, t.message.toString())
                    sendVersionServerToLiveData(loginLiveData)
                } else {
                    loginLiveData.postValue(
                        LiveDataObject(
                            Constants.Event.VERSION,
                            applicationContext.getString(R.string.server_connection_failure),
                            Constants.Server.CONNECTION_FAILURE
                        )
                    )
                    mServerRetryCount = Constants.Init.INT
                }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                setVersion(loginLiveData, response)
            }
        })
    }

    fun getVersionFromServer(): Version = mVersionFromServer

    private fun setVersion(loginLiveData: MutableLiveData<LiveDataObject>, response: Response<String>) {
        response.body()?.also {
            val listBody = it.split("_")
            val headers = response.headers()
            if (headers[Constants.Version.IS_WORKING]?.toBoolean() == true && listBody.size == 3) {
                mVersionFromServer = mVersionFromServer.apply {
                    //서버에서 받은 정보 넣기
                    this.app = headers[Constants.Version.APP] ?: Constants.Init.App
                    this.alert1 = listBody[1]
                    this.alert2 = listBody[2]
                    this.banner = headers[Constants.Version.BANNER] ?: Constants.Init.STRING
                }
                RetrofitService.BASE_URL = headers[Constants.Version.REDIRECT] ?: Constants.Init.STRING

                val liveData = LiveDataObject(Constants.Event.VERSION, Constants.Init.STRING, Constants.Server.CONNECTION_SUCCESS)
                checkAppVersion(liveData)
                loginLiveData.postValue(liveData)
            } else {
                //서버에서 막음
                loginLiveData.postValue(
                    LiveDataObject(
                        Constants.Event.VERSION,
                        listBody[0],
                        Constants.Server.CONNECTION_FAILURE
                    )
                )
            }
        }
    }

    private fun checkAppVersion(liveData: LiveDataObject) {
        mVersionFromServer.let { versionFromServer ->
            val latestVersion = versionFromServer.app
            if (latestVersion == Constants.Init.App){
                setLiveDataFail(liveData)
                return
            }

            val currentVersion = applicationContext.packageManager
                .getPackageInfo(applicationContext.packageName, 0).versionName
            if (latestVersion != currentVersion) {
                try {
                    val latest = latestVersion.split(".")
                    val current = currentVersion.split(".")
                    if (latest[0].toInt() > current[0].toInt()) {
                        //필수 업데이트
                        liveData.message = mVersionFromServer.alert1
                    } else if (latest[1].toInt() > current[1].toInt()) {
                        //일반 업데이트
                        liveData.message = mVersionFromServer.alert2
                    }
                } catch (e: Exception) {
                    setLiveDataFail(liveData)
                }
            }
        }
    }

    private fun setLiveDataFail(liveData : LiveDataObject){
        liveData.message = applicationContext.getString(R.string.server_connection_failure)
        liveData.result = Constants.Server.CONNECTION_FAILURE
    }
}