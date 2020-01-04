package space.jay.bingle.modules.permissions

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.modules.dialog.ManagerDialog

class ActivityPermissions : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val permission = intent.getStringArrayExtra(Constants.Intent.EXTRA_PERMISSION_REQUEST)
        val content: String? = intent.getStringExtra(Constants.Intent.EXTRA_PERMISSION_ALERT_CONTENT)
        getPermission(permission, content)
    }

    private fun getPermission(permissions: Array<String>, content: String?) {
        val needPermissions = ArrayList<String>()
        for (permission in permissions){
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                needPermissions.add(permission)
            }
        }
        if (needPermissions.isEmpty()){
            sendResult(true)
        } else {
            ManagerDialog().show(
                this,
                null,
                getString(R.string.permission_request),
                content+getString(R.string.permission_notification),
                getString(R.string.ok),
                {},
                DialogInterface.OnDismissListener {
                    requestPermissions(needPermissions.toTypedArray(), Constants.RequestCode.PERMISSION_REQUEST)
                },
                "request permission"
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.RequestCode.PERMISSION_REQUEST) {
            var result = grantResults.isNotEmpty()
            for (value in grantResults){
                if (value != PackageManager.PERMISSION_GRANTED){
                    result = false
                    break
                }
            }
            sendResult(result)
        }
    }

    private fun sendResult(result: Boolean) {
        val intent = Intent(Constants.Intent.ACTION_PERMISSION_RESULT).apply {
            putExtra(Constants.Intent.EXTRA_PERMISSION_RESULT, result)
        }
        sendBroadcast(intent)
        finish()
        overridePendingTransition(0,0)
    }
}
