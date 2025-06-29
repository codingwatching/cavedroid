package ru.fredboy.cavedroid.zygote.menu.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import ru.fredboy.cavedroid.common.CaveDroidConstants
import ru.fredboy.cavedroid.common.di.MenuScope
import ru.fredboy.cavedroid.common.utils.drawString
import ru.fredboy.cavedroid.domain.assets.usecase.GetFontUseCase
import ru.fredboy.cavedroid.domain.assets.usecase.GetStringHeightUseCase
import ru.fredboy.cavedroid.domain.assets.usecase.GetStringWidthUseCase
import ru.fredboy.cavedroid.domain.assets.usecase.GetTextureRegionByNameUseCase
import ru.fredboy.cavedroid.domain.configuration.repository.GameContextRepository
import ru.fredboy.cavedroid.domain.menu.model.MenuButton
import ru.fredboy.cavedroid.domain.menu.repository.MenuButtonRepository
import ru.fredboy.cavedroid.zygote.menu.action.IMenuAction
import ru.fredboy.cavedroid.zygote.menu.option.bool.IMenuBooleanOption
import javax.inject.Inject

@MenuScope
class MenuRenderer @Inject constructor(
    private val menuButtonRepository: MenuButtonRepository,
    private val gameContextRepository: GameContextRepository,
    private val getTextureRegionByName: GetTextureRegionByNameUseCase,
    private val menuButtonActions: Map<String, @JvmSuppressWildcards IMenuAction>,
    private val buttonBooleanOptions: Map<String, @JvmSuppressWildcards IMenuBooleanOption>,
    private val getFont: GetFontUseCase,
    private val getStringWidth: GetStringWidthUseCase,
    private val getStringHeight: GetStringHeightUseCase,
) {
    private val spriter = SpriteBatch()

    init {
        val cameraContext = requireNotNull(gameContextRepository.getCameraContext()) {
            "$TAG: CameraContext was not set"
        }

        spriter.projectionMatrix = cameraContext.camera.combined
    }

    private fun getButtonTextureRegionKey(button: MenuButton): String {
        val buttonAction = menuButtonActions[button.actionKey]

        if (buttonAction?.canPerform() != true) {
            return KEY_BUTTON_DISABLED_TEXTURE
        }

        return KEY_BUTTON_ENABLED_TEXTURE.takeIf { button.isEnabled } ?: KEY_BUTTON_DISABLED_TEXTURE
    }

    private fun renderButton(button: MenuButton, position: Int) {
        val textureRegion = getTextureRegionByName[getButtonTextureRegionKey(button)] ?: run {
            Gdx.app.error(TAG, "Couldn't render button because region not found")
            return
        }

        val label = when (button) {
            is MenuButton.Simple -> button.label
            is MenuButton.BooleanOption -> String.format(
                button.label,
                button.optionKeys.map { key -> buttonBooleanOptions[key]?.getOption().toString() }
            )
        }

        val buttonX = gameContextRepository.getWidth() / 2 - textureRegion.regionWidth / 2
        val buttonY = gameContextRepository.getHeight() / 4 + position.toFloat() * 30

        val buttonRect = Rectangle(
            /* x = */ buttonX,
            /* y = */ buttonY,
            /* width = */ textureRegion.regionWidth.toFloat(),
            /* height = */ textureRegion.regionHeight.toFloat(),
        )

        val inputCoordinates = gameContextRepository.getCameraContext()?.getViewportCoordinates(
            x = Gdx.input.x,
            y = Gdx.input.y,
        )

        spriter.draw(
            if (button.isEnabled && inputCoordinates != null && buttonRect.contains(
                    /* x = */ inputCoordinates.first,
                    /* y = */ inputCoordinates.second
                )
            ) {
                getTextureRegionByName[KEY_BUTTON_SELECTED_TEXTURE] ?: textureRegion
            } else {
                textureRegion
            },
            /* x = */ buttonX,
            /* y = */ buttonY,
        )

        spriter.drawString(
            font = getFont(),
            str = label,
            x = buttonX + textureRegion.regionWidth / 2 - getStringWidth(label) / 2,
            y = buttonY + textureRegion.regionHeight / 2 - getStringHeight(label),
        )
    }

    private fun drawBackground() {
        val backgroundRegion = getTextureRegionByName["background"] ?: return
        val gameLogo = getTextureRegionByName["gamelogo"] ?: return

        val backgroundRegionWidth = backgroundRegion.regionWidth
        val backgroundRegionHeight = backgroundRegion.regionWidth

        for (x in 0 .. gameContextRepository.getWidth().toInt() / backgroundRegionWidth) {
            for (y in 0 .. gameContextRepository.getHeight().toInt() / backgroundRegionHeight) {
                spriter.draw(
                    /* region = */ backgroundRegion,
                    /* x = */ x * backgroundRegionWidth.toFloat(),
                    /* y = */ y * backgroundRegionHeight.toFloat(),
                )
            }
        }

        spriter.draw(
            /* region = */ gameLogo,
            /* x = */ gameContextRepository.getWidth() / 2 - gameLogo.regionWidth.toFloat() / 2,
            /* y = */ 8f,
        )
    }

    fun render(delta: Float) {
        spriter.begin()
        drawBackground()

        menuButtonRepository.getCurrentMenuButtons()?.values
            ?.forEachIndexed { position, button -> renderButton(button, position) }

        spriter.drawString(
            font = getFont(),
            str = "CaveDroid " + CaveDroidConstants.VERSION,
            x = 0f,
            y = gameContextRepository.getHeight() - getStringHeight("CaveDroid " + CaveDroidConstants.VERSION) * 1.5f,
        );
        spriter.end()

    }

    fun dispose() {
        spriter.dispose()
    }

    companion object {
        private const val TAG = "MenuRenderer"

        private const val KEY_BUTTON_SELECTED_TEXTURE = "button_2"
        private const val KEY_BUTTON_ENABLED_TEXTURE = "button_1"
        private const val KEY_BUTTON_DISABLED_TEXTURE = "button_0"
    }

}