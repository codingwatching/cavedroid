package ru.fredboy.cavedroid.ux.controls.input.handler.keyboard

import ru.fredboy.cavedroid.common.di.GameScope
import ru.fredboy.cavedroid.domain.configuration.repository.GameContextRepository
import ru.fredboy.cavedroid.entity.mob.model.Player
import ru.fredboy.cavedroid.game.controller.mob.MobController
import ru.fredboy.cavedroid.ux.controls.input.IKeyboardInputHandler
import ru.fredboy.cavedroid.ux.controls.input.action.KeyboardInputAction
import ru.fredboy.cavedroid.ux.controls.input.action.keys.KeyboardInputActionKey
import ru.fredboy.cavedroid.ux.controls.input.annotation.BindKeyboardInputHandler
import javax.inject.Inject

@GameScope
@BindKeyboardInputHandler
class ToggleControlsModeKeyboardInputHandler @Inject constructor(
    private val gameContextRepository: GameContextRepository,
    private val mobController: MobController,
) : IKeyboardInputHandler {

    override fun checkConditions(action: KeyboardInputAction): Boolean {
        return action.actionKey is KeyboardInputActionKey.SwitchControlsMode && !action.isKeyDown
                && gameContextRepository.isTouch()
    }

    override fun handle(action: KeyboardInputAction) {
        if (mobController.player.controlMode == Player.ControlMode.WALK) {
            mobController.player.controlMode = Player.ControlMode.CURSOR
        } else {
            mobController.player.controlMode = Player.ControlMode.WALK
        }
    }

}