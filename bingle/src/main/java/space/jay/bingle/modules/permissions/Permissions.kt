package space.jay.bingle.modules.permissions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import space.jay.bingle.Constants

object Permissions {

    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var mPermissionListener: OnPermissionListener? = null

    fun getPermission(applicationContext: Context, permissionListener: OnPermissionListener, permissions: Array<String>, content: String?) {
        mPermissionListener = permissionListener
        registerReceiver(applicationContext)

        val intent = Intent(applicationContext, ActivityPermissions::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            putExtra(Constants.Intent.EXTRA_PERMISSION_REQUEST, permissions)
            content?.let { this.putExtra(Constants.Intent.EXTRA_PERMISSION_ALERT_CONTENT, it) }
        }

        applicationContext.startActivity(intent)
    }

    private fun registerReceiver(applicationContext: Context) {
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Constants.Intent.ACTION_PERMISSION_RESULT) {
                    result(applicationContext, intent)
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            this.addAction(Constants.Intent.ACTION_PERMISSION_RESULT)
        }
        applicationContext.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private fun result(applicationContext: Context, intent: Intent){
        mPermissionListener?.setResult(intent.getBooleanExtra(Constants.Intent.EXTRA_PERMISSION_RESULT, false))
        applicationContext.unregisterReceiver(mBroadcastReceiver)
        mBroadcastReceiver = null
        mPermissionListener = null
    }

    interface OnPermissionListener {
        fun setResult(result: Boolean)
    }
}