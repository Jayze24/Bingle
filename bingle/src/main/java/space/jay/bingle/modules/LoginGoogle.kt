package space.jay.bingle.modules

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import space.jay.bingle.Constants
import space.jay.bingle.R
import space.jay.bingle.ui.login.ViewModelLogin
import space.jay.bingle.data.LiveDataObject
import space.jay.bingle.data.User

class LoginGoogle(
    private val boxUser: BoxUser,
    private val activity: AppCompatActivity
) {

    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mGoogleSignInClient by lazy {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    fun getUser(/*toGoActivity: Class<*>?*/) = mAuth.currentUser?.let {
        val data = User().apply {
            uid = it.uid
            name = it.displayName ?: ""
            email = it.email ?: ""
        }
        boxUser.setData(data)
        true
    } ?: false

    fun result(requestCode: Int, data: Intent?, viewModelLogin: ViewModelLogin) {
        if (requestCode == Constants.RequestCode.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                task.getResult(ApiException::class.java)?.also {
                    //구글 인증 확인
                    getCredential(it, viewModelLogin)
                }
            } else {
                //로그인 실패
                viewModelLogin.getMessageObservable().value = LiveDataObject(
                    Constants.Event.LOGIN_GOOGLE,
                    activity.getString(R.string.login_failure),
                    Constants.Server.CONNECTION_FAILURE
                )
            }
        }
    }

    fun signInGoogle() {
        activity.startActivityForResult(mGoogleSignInClient.signInIntent, Constants.RequestCode.GOOGLE_SIGN_IN)
    }

    private fun getCredential(
        googleSignInAccount: GoogleSignInAccount,
        viewModelLogin: ViewModelLogin
    ) {
        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    if (getUser()) {
                        viewModelLogin.getMessageObservable().value = LiveDataObject(
                            Constants.Event.LOGIN_GOOGLE,
                            Constants.Server.CONNECTION_SUCCESS
                        )
                    } else {
                        //유정 정보 디비 저장 실패
                        viewModelLogin.getMessageObservable().value = LiveDataObject(
                            Constants.Event.LOGIN_GOOGLE,
                            activity.getString(R.string.db_user_failure),
                            Constants.Server.CONNECTION_FAILURE
                        )
                    }
                } else {
                    //인증 실패
                    viewModelLogin.getMessageObservable().value = LiveDataObject(
                        Constants.Event.LOGIN_GOOGLE,
                        activity.getString(R.string.login_failure),
                        Constants.Server.CONNECTION_FAILURE
                    )
                }
            }
    }
}