package space.jay.bingle

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import space.jay.bingle.di.DaggerComponentAppInit
import javax.inject.Inject

class AppInit : Application(), HasAndroidInjector{

    @Inject
    lateinit var mAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerComponentAppInit
            .builder()
            .application(this)
            .build()
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = mAndroidInjector
}