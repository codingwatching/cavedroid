package ru.deadsoftware.cavedroid.game.input.handler.mouse

import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindMouseInputHandler
import ru.deadsoftware.cavedroid.game.GameItemsHolder
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.GameUiWindow
import ru.deadsoftware.cavedroid.game.ui.windows.GameWindowsManager
import ru.deadsoftware.cavedroid.game.input.IMouseInputHandler
import ru.deadsoftware.cavedroid.game.input.action.MouseInputAction
import ru.deadsoftware.cavedroid.game.input.action.keys.MouseInputActionKey
import ru.deadsoftware.cavedroid.game.input.isInsideWindow
import ru.deadsoftware.cavedroid.game.mobs.MobsController
import ru.deadsoftware.cavedroid.game.ui.windows.GameWindowsConfigs
import ru.deadsoftware.cavedroid.misc.Assets
import javax.inject.Inject

@GameScope
@BindMouseInputHandler
class SelectCreativeInventoryItemMouseInputHandler @Inject constructor(
    private val gameItemsHolder: GameItemsHolder,
    private val gameWindowsManager: GameWindowsManager,
    private val mobsController: MobsController,
) : IMouseInputHandler {

    private val creativeInventoryTexture get() = requireNotNull(Assets.textureRegions["creative"])

    override fun checkConditions(action: MouseInputAction): Boolean {
        return gameWindowsManager.getCurrentWindow() == GameUiWindow.CREATIVE_INVENTORY &&
                !gameWindowsManager.isDragging &&
                (action.actionKey is MouseInputActionKey.Left || action.actionKey is MouseInputActionKey.Screen) &&
                action.actionKey.touchUp && isInsideWindow(action, creativeInventoryTexture)
    }

    override fun handle(action: MouseInputAction) {
        val creativeTexture = creativeInventoryTexture
        val xOnGrid = (action.screenX - (action.cameraViewport.width / 2 - creativeTexture.regionWidth / 2 +
                GameWindowsConfigs.Creative.itemsGridMarginLeft)) /
                GameWindowsConfigs.Creative.itemsGridColWidth
        val yOnGrid = (action.screenY - (action.cameraViewport.height / 2 - creativeTexture.regionHeight / 2 +
                GameWindowsConfigs.Creative.itemsGridMarginTop)) /
                GameWindowsConfigs.Creative.itemsGridRowHeight

        if (xOnGrid < 0 || xOnGrid >= GameWindowsConfigs.Creative.itemsInRow ||
            yOnGrid < 0 || yOnGrid >= GameWindowsConfigs.Creative.itemsInCol) {
            return
        }

        val itemIndex = (gameWindowsManager.creativeScrollAmount * GameWindowsConfigs.Creative.itemsInRow +
                (xOnGrid.toInt() + yOnGrid.toInt() * GameWindowsConfigs.Creative.itemsInRow))
        val item = gameItemsHolder.getItemFromCreativeInventory(itemIndex)
        mobsController.player.inventory.addItem(item)
    }

}