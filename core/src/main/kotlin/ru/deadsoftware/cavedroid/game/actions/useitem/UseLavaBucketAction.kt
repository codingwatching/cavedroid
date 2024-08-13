package ru.deadsoftware.cavedroid.game.actions.useitem

import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindUseItemAction
import ru.fredboy.cavedroid.common.di.GameScope
import ru.fredboy.cavedroid.domain.items.model.item.Item
import ru.fredboy.cavedroid.domain.items.usecase.GetBlockByKeyUseCase
import ru.fredboy.cavedroid.domain.items.usecase.GetItemByKeyUseCase
import ru.fredboy.cavedroid.game.controller.mob.MobController
import ru.fredboy.cavedroid.game.world.GameWorld
import javax.inject.Inject

@GameScope
@BindUseItemAction(UseLavaBucketAction.ACTION_KEY)
class UseLavaBucketAction @Inject constructor(
    private val gameWorld: GameWorld,
    private val mobController: MobController,
    private val getBlockByKeyUseCase: GetBlockByKeyUseCase,
    private val getItemByKeyUseCase: GetItemByKeyUseCase,
) : IUseItemAction {

    override fun perform(item: Item.Usable, x: Int, y: Int) {
        gameWorld.placeToForeground(x, y, getBlockByKeyUseCase["lava"])

        if (mobController.player.gameMode != 1) {
            mobController.player.setCurrentInventorySlotItem(getItemByKeyUseCase["bucket_empty"])
        }
    }

    companion object {
        const val ACTION_KEY = "use_lava_bucket"
    }
}