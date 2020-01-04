package space.jay.bingle.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import space.jay.bingle.ui.login.ActivityLogin
import space.jay.bingle.modules.BoxUser
import space.jay.bingle.modules.LoginGoogle
import space.jay.bingle.modules.ManagerVersion

@Module
class ModuleLogin {

    @ScopeActivity
    @Provides
    fun providesLogin(boxUser: BoxUser, activityLogin: ActivityLogin) =
        LoginGoogle(boxUser, activityLogin as AppCompatActivity)

    @ScopeActivity
    @Provides
    fun providesManagerVersion(applicationContext: Context) = ManagerVersion(applicationContext)
}