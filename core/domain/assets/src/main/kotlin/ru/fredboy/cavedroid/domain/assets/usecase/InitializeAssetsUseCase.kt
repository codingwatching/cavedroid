package ru.fredboy.cavedroid.domain.assets.usecase

import dagger.Reusable
import ru.fredboy.cavedroid.domain.assets.GameAssetsHolder
import javax.inject.Inject

@Reusable
class InitializeAssetsUseCase @Inject constructor(
    private val gameAssetsHolder: GameAssetsHolder,
) {

    operator fun invoke() {
        gameAssetsHolder.initializeRepository()
    }

}