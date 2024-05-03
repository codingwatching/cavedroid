package ru.deadsoftware.cavedroid.game.input.handler.mouse

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Timer
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.GameUiWindow
import ru.deadsoftware.cavedroid.game.actions.placeToBackgroundAction
import ru.deadsoftware.cavedroid.game.actions.placeToForegroundAction
import ru.deadsoftware.cavedroid.game.actions.placeblock.IPlaceBlockAction
import ru.deadsoftware.cavedroid.game.actions.useitem.IUseItemAction
import ru.deadsoftware.cavedroid.game.input.IGameInputHandler
import ru.deadsoftware.cavedroid.game.input.action.MouseInputAction
import ru.deadsoftware.cavedroid.game.input.action.keys.MouseInputActionKey
import ru.deadsoftware.cavedroid.game.input.isInsideHotbar
import ru.deadsoftware.cavedroid.game.mobs.MobsController
import ru.deadsoftware.cavedroid.game.model.item.Item
import ru.deadsoftware.cavedroid.game.windows.GameWindowsManager
import javax.inject.Inject

@GameScope
class UseItemMouseInputHandler @Inject constructor(
    private val mobsController: MobsController,
    private val useItemActionMap: Map<String, @JvmSuppressWildcards IUseItemAction>,
    private val placeBlockActionMap: Map<String, @JvmSuppressWildcards IPlaceBlockAction>,
    private val gameWindowsManager: GameWindowsManager,
) : IGameInputHandler<MouseInputAction> {

    private var buttonHoldTask: Timer.Task? = null

    override fun checkConditions(action: MouseInputAction): Boolean {
        return buttonHoldTask?.isScheduled == true ||
                !isInsideHotbar(action) &&
                gameWindowsManager.getCurrentWindow() == GameUiWindow.NONE &&
                action.actionKey is MouseInputActionKey.Right
    }

    private fun cancelHold() {
        buttonHoldTask?.cancel()
        buttonHoldTask = null
    }

    private fun handleHold(action: MouseInputAction) {
        cancelHold()

        val player = mobsController.player
        val item = player.currentItem.item
        player.startHitting(false)
        player.stopHitting()

        if (item is Item.Placeable) {
            placeBlockActionMap.placeToBackgroundAction(
                item = player.currentItem.item as Item.Placeable,
                x = player.cursorX,
                y = player.cursorY
            )
        }
    }

    private fun handleDown(action: MouseInputAction) {
        cancelHold()
        buttonHoldTask = object : Timer.Task() {
            override fun run() {
                handleHold(action)
            }

        }
        Timer.schedule(buttonHoldTask, TOUCH_HOLD_TIME_SEC)
    }

    private fun handleUp(action: MouseInputAction) {
        val player = mobsController.player
        val item = player.currentItem.item
        cancelHold()

        player.startHitting(false)
        player.stopHitting()

        if (item is Item.Placeable) {
            placeBlockActionMap.placeToForegroundAction(
                item = item,
                x = player.cursorX,
                y = player.cursorY
            )
        } else if (item is Item.Usable) {
            useItemActionMap[item.useActionKey]?.perform(item, player.cursorX, player.cursorY)
                ?: Gdx.app.error(TAG, "use item action ${item.useActionKey} not found");
        }
    }

    override fun handle(action: MouseInputAction) {
        if (action.actionKey !is MouseInputActionKey.Right) {
            if (buttonHoldTask?.isScheduled == true) {
                cancelHold()
            }
            return
        }

        if (action.actionKey.touchUp && buttonHoldTask?.isScheduled == true) {
            handleUp(action)
        } else if (!action.actionKey.touchUp) {
            handleDown(action)
        }
    }

    companion object {
        private const val TAG = "UseItemMouseInputActionHandler"
        private const val TOUCH_HOLD_TIME_SEC = 0.5f
    }
}