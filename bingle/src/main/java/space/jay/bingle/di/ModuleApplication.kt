package space.jay.bingle.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModuleApplication {

    @Provides
    @Singleton
    fun providesContext(application: Application): Context = application.applicationContext
}