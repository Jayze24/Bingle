package space.jay.bingle.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import space.jay.bingle.data.MyObjectBox
import space.jay.bingle.modules.BoxUser
import javax.inject.Singleton

@Module
class ModuleObjectBox {

    @Singleton
    @Provides
    fun providesObjectBox(applicationContext: Context): BoxStore =
        MyObjectBox.builder().androidContext(applicationContext).build()

    @Singleton
    @Provides
    fun providesBoxUser(boxStore: BoxStore) = BoxUser(boxStore)
}