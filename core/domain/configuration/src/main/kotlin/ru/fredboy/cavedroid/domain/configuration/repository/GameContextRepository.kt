package ru.fredboy.cavedroid.domain.configuration.repository

import ru.fredboy.cavedroid.common.model.Joystick
import ru.fredboy.cavedroid.domain.configuration.model.CameraContext

interface GameContextRepository {

    fun isTouch(): Boolean

    fun getGameDirectory(): String

    fun getWidth(): Float

    fun getHeight(): Float

    fun shouldShowInfo(): Boolean

    fun shouldShowMap(): Boolean

    fun getJoystick(): Joystick?

    fun isFullscreen(): Boolean

    fun useDynamicCamera(): Boolean

    fun getCameraContext(): CameraContext?

    fun setTouch(isTouch: Boolean)

    fun setGameDirectory(path: String)

    fun setWidth(width: Float)

    fun setHeight(height: Float)

    fun setShowInfo(show: Boolean)

    fun setShowMap(show: Boolean)

    fun setJoystick(joystick: Joystick?)

    fun setFullscreen(fullscreen: Boolean)

    fun setUseDynamicCamera(use: Boolean)

    fun setCameraContext(cameraContext: CameraContext?)

}
