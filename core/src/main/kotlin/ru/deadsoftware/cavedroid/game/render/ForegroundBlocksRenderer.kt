package ru.deadsoftware.cavedroid.game.render

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.mobs.MobsController
import ru.deadsoftware.cavedroid.game.world.GameWorld
import ru.deadsoftware.cavedroid.misc.annotations.multibinding.BindRenderer
import ru.deadsoftware.cavedroid.misc.utils.forEachBlockInArea
import javax.inject.Inject

@GameScope
@BindRenderer
class ForegroundBlocksRenderer @Inject constructor(
    gameWorld: GameWorld,
    mobsController: MobsController
) : BlocksRenderer(gameWorld, mobsController) {

    override val renderLayer get() = RENDER_LAYER

    override val background = false

    override fun draw(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer, viewport: Rectangle, delta: Float) {
        forEachBlockInArea(viewport) { x, y ->
            drawForeMap(spriteBatch, viewport, x, y)
        }
        drawBlockDamage(spriteBatch, viewport)
    }

    companion object {
        private const val RENDER_LAYER = 100400
    }
}