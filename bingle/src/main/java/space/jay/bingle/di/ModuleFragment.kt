package space.jay.bingle.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import space.jay.bingle.ui.game.board.FragmentBoardBasic

@Module
abstract class ModuleFragment {

    @ScopeFragment
    @ContributesAndroidInjector
    abstract fun fragmentBoardBasic(): FragmentBoardBasic
}