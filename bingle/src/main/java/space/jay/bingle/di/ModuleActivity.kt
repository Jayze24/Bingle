package space.jay.bingle.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import space.jay.bingle.ui.login.ActivityLogin

@Module
abstract class ModuleActivity {

    @ScopeActivity
    @ContributesAndroidInjector(modules = [ModuleLogin::class])
    abstract fun activityLogin(): ActivityLogin
}