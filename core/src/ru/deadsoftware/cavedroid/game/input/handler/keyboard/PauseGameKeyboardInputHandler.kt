package ru.deadsoftware.cavedroid.game.input.handler.keyboard

import ru.deadsoftware.cavedroid.MainConfig
import ru.deadsoftware.cavedroid.game.GameSaver
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.GameUiWindow
import ru.deadsoftware.cavedroid.game.input.IGameInputHandler
import ru.deadsoftware.cavedroid.game.input.action.KeyboardInputAction
import ru.deadsoftware.cavedroid.game.input.action.keys.KeyboardInputActionKey
import ru.deadsoftware.cavedroid.game.mobs.MobsController
import ru.deadsoftware.cavedroid.game.objects.drop.DropController
import ru.deadsoftware.cavedroid.game.objects.container.ContainerController
import ru.deadsoftware.cavedroid.game.ui.windows.GameWindowsManager
import ru.deadsoftware.cavedroid.game.world.GameWorld
import javax.inject.Inject

@GameScope
class PauseGameKeyboardInputHandler @Inject constructor(
    private val mainConfig: MainConfig,
    private val dropController: DropController,
    private val mobsController: MobsController,
    private val gameWorld: GameWorld,
    private val containerController: ContainerController,
    private val gameWindowsManager: GameWindowsManager,
) : IGameInputHandler<KeyboardInputAction> {

    override fun checkConditions(action: KeyboardInputAction): Boolean {
        return action.actionKey is KeyboardInputActionKey.Pause && action.isKeyDown
    }

    override fun handle(action: KeyboardInputAction) {
        if (gameWindowsManager.getCurrentWindow() != GameUiWindow.NONE) {
            gameWindowsManager.closeWindow()
            return
        }

        GameSaver.save(mainConfig, dropController, mobsController, containerController, gameWorld)
        mainConfig.caveGame.quitGame()
    }
}