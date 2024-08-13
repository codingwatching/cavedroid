package ru.deadsoftware.cavedroid.game.actions.updateblock

import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindUpdateBlockAction
import ru.fredboy.cavedroid.common.di.GameScope
import ru.fredboy.cavedroid.domain.items.usecase.GetBlockByKeyUseCase
import ru.fredboy.cavedroid.game.world.GameWorld
import javax.inject.Inject

@GameScope
@BindUpdateBlockAction(stringKey = UpdateSnowedGrassAction.BLOCK_KEY)
class UpdateSnowedGrassAction @Inject constructor(
    private val gameWorld: GameWorld,
    private val getBlockByKeyUseCase: GetBlockByKeyUseCase,
) : IUpdateBlockAction {

    override fun update(x: Int, y: Int) {
        val blockOnTop = gameWorld.getForeMap(x, y - 1)
        val makesDirt = blockOnTop.params.hasCollision || blockOnTop.isFluid()

        when {
            makesDirt -> gameWorld.setForeMap(x, y, getBlockByKeyUseCase["dirt"])
            blockOnTop.params.key != "snow" -> gameWorld.setForeMap(x, y, getBlockByKeyUseCase["grass"])
        }
    }

    companion object {
        const val BLOCK_KEY = "grass_snowed"
    }
}