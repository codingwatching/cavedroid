package ru.deadsoftware.cavedroid.game.objects.drop

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.deadsoftware.cavedroid.game.GameItemsHolder
import ru.deadsoftware.cavedroid.game.model.item.Item

class Drop @JvmOverloads constructor(
    x: Float,
    y: Float,
    _item: Item,
    _amount: Int = 1,
) : Rectangle(x, y, DROP_SIZE, DROP_SIZE) {

    var amount: Int = _amount
        private set

    val itemKey = _item.params.key
    val velocity = getInitialVelocity()
    var pickedUp = false

    @Suppress("UNNECESSARY_LATEINIT")
    @Transient
    lateinit var item: Item
        private set

    init {
        item = _item
    }

    fun initItem(gameItemsHolder: GameItemsHolder) {
        if (this::item.isInitialized) {
            return
        }

        item = gameItemsHolder.getItem(itemKey)
    }

    fun canMagnetTo(rectangle: Rectangle): Boolean {
        val magnetArea = getMagnetArea()
        return Intersector.overlaps(magnetArea, rectangle)
    }

    @JvmOverloads
    fun subtract(count: Int = 1) {
        if (count < 0) {
            throw IllegalArgumentException("Can't subtract negative amount")
        }

        amount -= count
    }

    private fun getMagnetArea(): Rectangle {
        return Rectangle(
            /* x = */ x - MAGNET_DISTANCE,
            /* y = */ y - MAGNET_DISTANCE,
            /* width = */ width + MAGNET_DISTANCE * 2,
            /* height = */ height + MAGNET_DISTANCE * 2,
        )
    }

    companion object {
        private const val MAGNET_DISTANCE = 8f

        const val MAGNET_VELOCITY = 256f
        const val DROP_SIZE = 8f

        private fun getInitialVelocity(): Vector2 = Vector2(0f, -1f)
    }
}