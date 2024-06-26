package ru.deadsoftware.cavedroid.game.actions.updateblock

import ru.deadsoftware.cavedroid.game.GameItemsHolder
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.world.GameWorld
import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindUpdateBlockAction
import javax.inject.Inject

@GameScope
@BindUpdateBlockAction(stringKey = UpdateBedRightAction.BLOCK_KEY)
class UpdateBedRightAction @Inject constructor(
    private val gameWorld: GameWorld,
    private val gameItemsHolder: GameItemsHolder,
) : IUpdateBlockAction {

    override fun update(x: Int, y: Int) {
        val bedLeft = gameItemsHolder.getBlock("bed_l")
        if (gameWorld.getForeMap(x - 1, y) != bedLeft) {
            gameWorld.resetForeMap(x, y)
        }
    }

    companion object {
        const val BLOCK_KEY = "bed_r"
    }
}