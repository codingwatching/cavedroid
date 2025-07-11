package ru.fredboy.cavedroid.domain.assets.usecase

import com.badlogic.gdx.graphics.Texture
import dagger.Reusable
import ru.fredboy.cavedroid.domain.assets.GameAssetsHolder
import javax.inject.Inject

@Reusable
class GetItemTextureUseCase @Inject constructor(
    private val gameAssetsHolder: GameAssetsHolder,
) {

    operator fun get(name: String): Texture = gameAssetsHolder.getItemTexture(name)
}
