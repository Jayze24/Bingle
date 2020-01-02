package space.jay.bingle.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import space.jay.bingle.ui.login.ActivityLogin
import space.jay.bingle.modules.BoxUser
import space.jay.bingle.modules.LoginGoogle

@Module
class ModuleLogin {

    @ScopeActivity
    @Provides
    fun providesLogin(boxUser: BoxUser, activityLogin: ActivityLogin) =
        LoginGoogle(boxUser, activityLogin as AppCompatActivity)
}