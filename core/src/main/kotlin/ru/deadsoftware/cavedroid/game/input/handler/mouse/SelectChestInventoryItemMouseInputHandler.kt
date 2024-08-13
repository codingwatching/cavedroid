package ru.deadsoftware.cavedroid.game.input.handler.mouse

import ru.deadsoftware.cavedroid.game.GameUiWindow
import ru.deadsoftware.cavedroid.game.input.action.MouseInputAction
import ru.deadsoftware.cavedroid.game.ui.windows.GameWindowsConfigs
import ru.deadsoftware.cavedroid.game.ui.windows.GameWindowsManager
import ru.deadsoftware.cavedroid.game.ui.windows.inventory.ChestInventoryWindow
import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindMouseInputHandler
import ru.fredboy.cavedroid.common.di.GameScope
import ru.fredboy.cavedroid.domain.assets.usecase.GetTextureRegionByNameUseCase
import ru.fredboy.cavedroid.domain.items.repository.ItemsRepository
import ru.fredboy.cavedroid.game.controller.mob.MobController
import javax.inject.Inject

@GameScope
@BindMouseInputHandler
class SelectChestInventoryItemMouseInputHandler @Inject constructor(
    private val gameWindowsManager: GameWindowsManager,
    private val mobController: MobController,
    private val textureRegions: GetTextureRegionByNameUseCase,
    itemsRepository: ItemsRepository,
) : AbstractInventoryItemsMouseInputHandler(itemsRepository, gameWindowsManager, GameUiWindow.CHEST) {

    override val windowTexture get() = requireNotNull(textureRegions["chest"])

    private fun handleInsideContentGrid(action: MouseInputAction, xOnGrid: Int, yOnGrid: Int) {
        val window = gameWindowsManager.currentWindow as ChestInventoryWindow
        val itemIndex = xOnGrid + yOnGrid * GameWindowsConfigs.Chest.contentsInRow

        handleInsidePlaceableCell(action, window.chest.items, window, itemIndex)
    }

    private fun handleInsideInventoryGrid(action: MouseInputAction, xOnGrid: Int, yOnGrid: Int) {
        val window = gameWindowsManager.currentWindow as ChestInventoryWindow

        var itemIndex = xOnGrid + yOnGrid * GameWindowsConfigs.Chest.itemsInRow
        itemIndex += GameWindowsConfigs.Chest.hotbarCells

        if (itemIndex >= mobController.player.inventory.size) {
            itemIndex -= mobController.player.inventory.size
        }

        handleInsidePlaceableCell(action, mobController.player.inventory.items, window, itemIndex)
    }

    override fun handle(action: MouseInputAction) {
        val texture = windowTexture

        val xOnWindow = action.screenX - (action.cameraViewport.width / 2 - texture.regionWidth / 2)
        val yOnWindow = action.screenY - (action.cameraViewport.height / 2 - texture.regionHeight / 2)

        val xOnGrid = (xOnWindow - GameWindowsConfigs.Chest.itemsGridMarginLeft) /
                GameWindowsConfigs.Chest.itemsGridColWidth
        val yOnGrid = (yOnWindow - GameWindowsConfigs.Chest.itemsGridMarginTop) /
                GameWindowsConfigs.Chest.itemsGridRowHeight

        val xOnContent = (xOnWindow - GameWindowsConfigs.Chest.contentsMarginLeft) /
                GameWindowsConfigs.Chest.itemsGridColWidth
        val yOnContent = (yOnWindow - GameWindowsConfigs.Chest.contentsMarginTop) /
                GameWindowsConfigs.Chest.itemsGridRowHeight

        val isInsideInventoryGrid = xOnGrid >= 0 && xOnGrid < GameWindowsConfigs.Chest.itemsInRow &&
                yOnGrid >= 0 && yOnGrid < GameWindowsConfigs.Chest.itemsInCol

        val isInsideContentGrid = xOnContent >= 0 && xOnContent < GameWindowsConfigs.Chest.contentsInRow &&
                yOnContent >= 0 && yOnContent < GameWindowsConfigs.Chest.contentsInCol


        if (isInsideInventoryGrid) {
            handleInsideInventoryGrid(action, xOnGrid.toInt(), yOnGrid.toInt())
        } else if (isInsideContentGrid) {
            handleInsideContentGrid(action, xOnContent.toInt(), yOnContent.toInt())
        }
    }
}