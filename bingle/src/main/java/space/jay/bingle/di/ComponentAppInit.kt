package space.jay.bingle.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import space.jay.bingle.AppInit
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ModuleApplication::class,
        ModuleActivity::class,
        ModuleObjectBox::class]
)
interface ComponentAppInit : AndroidInjector<AppInit> {

    fun getApplicationContext(): Context

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ComponentAppInit
    }
}