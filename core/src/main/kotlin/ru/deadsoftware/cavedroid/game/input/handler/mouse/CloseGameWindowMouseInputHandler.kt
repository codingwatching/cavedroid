package ru.deadsoftware.cavedroid.game.input.handler.mouse

import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindMouseInputHandler
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.GameUiWindow
import ru.deadsoftware.cavedroid.game.ui.windows.GameWindowsManager
import ru.deadsoftware.cavedroid.game.input.IMouseInputHandler
import ru.deadsoftware.cavedroid.game.input.action.MouseInputAction
import ru.deadsoftware.cavedroid.game.input.action.keys.MouseInputActionKey
import ru.deadsoftware.cavedroid.game.input.isInsideWindow
import ru.deadsoftware.cavedroid.game.mobs.MobsController
import ru.deadsoftware.cavedroid.game.objects.drop.DropController
import ru.deadsoftware.cavedroid.misc.Assets
import javax.inject.Inject

@GameScope
@BindMouseInputHandler
class CloseGameWindowMouseInputHandler @Inject constructor(
    private val gameWindowsManager: GameWindowsManager,
    private val mobsController: MobsController,
    private val dropController: DropController,
) : IMouseInputHandler {

    private val creativeInventoryTexture get() = requireNotNull(Assets.textureRegions["creative"])
    private val survivalInventoryTexture get() = requireNotNull(Assets.textureRegions["survival"])
    private val craftingInventoryTexture get() = requireNotNull(Assets.textureRegions["crafting_table"])
    private val furnaceInventoryTexture get() = requireNotNull(Assets.textureRegions["furnace"])
    private val chestInventoryTexture get() = requireNotNull(Assets.textureRegions["chest"])

    override fun checkConditions(action: MouseInputAction): Boolean {
        return gameWindowsManager.getCurrentWindow() != GameUiWindow.NONE &&
                (action.actionKey is MouseInputActionKey.Left || action.actionKey is MouseInputActionKey.Screen) &&
                action.actionKey.touchUp &&
                !isInsideWindow(action, getCurrentWindowTexture())
    }

    private fun getCurrentWindowTexture(): TextureRegion {
        return when (val window = gameWindowsManager.getCurrentWindow()) {
            GameUiWindow.CREATIVE_INVENTORY -> creativeInventoryTexture
            GameUiWindow.SURVIVAL_INVENTORY -> survivalInventoryTexture
            GameUiWindow.CRAFTING_TABLE -> craftingInventoryTexture
            GameUiWindow.FURNACE -> furnaceInventoryTexture
            GameUiWindow.CHEST -> chestInventoryTexture
            else -> throw UnsupportedOperationException("Cant close window ${window.name}")
        }
    }

    override fun handle(action: MouseInputAction) {
        val selectedItem = gameWindowsManager.currentWindow?.selectedItem
        if (selectedItem != null) {
                dropController.addDrop(
                    /* x = */ mobsController.player.x + (32f * mobsController.player.direction.basis),
                    /* y = */ mobsController.player.y,
                    /* item = */ selectedItem.item,
                    /* count = */ selectedItem.amount,
                )
            gameWindowsManager.currentWindow?.selectedItem = null
        }
        gameWindowsManager.closeWindow()
    }

}