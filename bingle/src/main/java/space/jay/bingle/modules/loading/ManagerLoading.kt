package space.jay.bingle.modules.loading

import android.content.Context
import android.content.Intent

object ManagerLoading {

    private var mIsShowing = false
    private var mActivityLoading : ActivityLoading? = null

    fun show(context: Context){
        val intent = Intent(context, ActivityLoading::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
        context.startActivity(intent)
    }

    fun dismiss() {
        mActivityLoading?.also {
            it.finish()
            mActivityLoading = null
        }
    }

    fun setLoadingActivity(activityLoading: ActivityLoading?){
        mIsShowing = activityLoading != null
        mActivityLoading = activityLoading
    }

    fun isShowing() = mIsShowing

    /**
     * window manager 에 surface view 추가 하는 코드
     * 현재 사용하지 않음
     *
     * 사용하려면 mainifests 에 아래 permission 추가
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
     *
     * overlay 허용 인텐트 요청 할 것
     * val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity.packageName}"))
     * activity.startActivityForResult(intent, Constants.RequestCode.PERMISSION_REQUEST)
     *
     *
     */

//    private var mWindowManager : WindowManager? = null
//    private var mSurfaceView : SurfaceView? = null
//
//    fun show(context: Context) : Boolean{
//        val type = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//        } else {
//            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
//        }
//        mSurfaceView = ViewLoading(context, null)
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            type,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            PixelFormat.TRANSLUCENT
//        )
//
//        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        mWindowManager?.let {
//            it.addView(mSurfaceView, params)
//            mIsShowing = true
//        }
//        return mIsShowing
//    }
//
//    fun dismiss(){
//        mWindowManager?.also { windowManager ->
//            mSurfaceView?.also {
//                windowManager.removeView(it)
//            }
//        }
//        mWindowManager = null
//        mSurfaceView = null
//        mIsShowing = false
//    }
}