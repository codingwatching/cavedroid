package ru.fredboy.cavedroid.game.controller.mob.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ru.fredboy.cavedroid.common.utils.applyOrigin
import ru.fredboy.cavedroid.common.utils.drawSprite
import ru.fredboy.cavedroid.domain.assets.model.MobSprite
import ru.fredboy.cavedroid.domain.items.model.item.InventoryItem
import ru.fredboy.cavedroid.domain.items.model.item.Item
import ru.fredboy.cavedroid.domain.items.repository.ItemsRepository
import ru.fredboy.cavedroid.domain.items.usecase.GetFallbackItemUseCase
import ru.fredboy.cavedroid.domain.items.usecase.GetItemByKeyUseCase

class Player(
    private val sprite: MobSprite.Player,
    private val getFallbackItem: GetFallbackItemUseCase,
    x: Float,
    y: Float,
) : Mob(x, y, WIDTH, HEIGHT, Direction.random(), MAX_HEALTH, {}) {

    var inventory = Inventory(
        size = INVENTORY_SIZE,
        hotbarSize = HOTBAR_SIZE,
        fallbackItem = getFallbackItem(),
    )

    var gameMode = 0
    var swim = false

    var cursorX = 0
    var cursorY = 0

    var controlMode = ControlMode.WALK
    var blockDamage = 0f

    var isHitting = false
    var isHittingWithDamage = false

    var hitAnim = 0f
    var hitAnimDelta = ANIMATION_SPEED

    var headRotation = 0f

    override val speed get() = SPEED

    override fun changeDir() {
        switchDir()
        velocity = Vector2(direction.basis * speed, 0f)
    }

    override fun jump() {
        velocity.y = JUMP_VELOCITY
    }

    override fun damage(damage: Int) {
        super.damage(damage)

        if (damage > 0 && canJump) {
            jump()
        }
    }

    override fun getDropItems(itemByKey: GetItemByKeyUseCase): List<InventoryItem> {
        return inventory.items
    }

    override fun draw(spriteBatch: SpriteBatch, x: Float, y: Float, delta: Float) {
        with(sprite) {
            hand.setFlip(looksRight(), hand.isFlipY)
            leg.setFlip(looksRight(), leg.isFlipY)
            head.setFlip(looksRight(), head.isFlipY)
            body.setFlip(looksRight(), body.isFlipY)

            hand.setOrigin(hand.width / 2f, 0f)
            leg.setOrigin(leg.width / 2f, 0f)
            head.setOrigin(head.width / 2, head.height)

            var backHandAnim = 0f
            var frontHandAnim = 0f

            val rightHandAnim = getRightHandAnim(delta)

            if (looksLeft()) {
                backHandAnim = rightHandAnim
                frontHandAnim = anim
            } else {
                backHandAnim = -anim
                frontHandAnim = -rightHandAnim
            }

            val backgroundTintColor = tintColor.cpy().sub(Color(0xAAAAAA shl 16))

            hand.color = backgroundTintColor
            spriteBatch.drawSprite(hand, x + getBodyRelativeX(), y + getBodyRelativeY(), backHandAnim)

            if (looksLeft()) {
                drawItem(spriteBatch, x, y, -backHandAnim)
            }

            leg.color = backgroundTintColor
            spriteBatch.drawSprite(leg, x + getBodyRelativeX(), y + getLegsRelativeY(), anim)

            leg.color = tintColor
            spriteBatch.drawSprite(leg, x + getBodyRelativeX(), y + getLegsRelativeY(), -anim)

            head.color = tintColor
            spriteBatch.drawSprite(head, x, y, headRotation)

            body.color = tintColor
            spriteBatch.drawSprite(body, x + getBodyRelativeX(), y + getBodyRelativeY())

            if (looksRight()) {
                drawItem(spriteBatch, x, y, frontHandAnim)
            }

            hand.color = tintColor
            spriteBatch.drawSprite(hand, x + getBodyRelativeX(), y + getBodyRelativeY(), frontHandAnim)
        }
    }

    fun startHitting(withDamage: Boolean = true) {
        if (isHitting) {
            return
        }

        isHitting = true
        isHittingWithDamage = withDamage
        hitAnim = HIT_ANIMATION_RANGE.endInclusive
        hitAnimDelta = ANIMATION_SPEED
    }

    fun stopHitting() {
        blockDamage = 0f
        isHitting = false
    }

    fun decreaseCurrentItemCount() {
        if (gameMode == 1) {
            return
        }

        inventory.activeItem.subtract()

        if (inventory.activeItem.amount <= 0) {
            setCurrentInventorySlotItem(getFallbackItem())
        }
    }

    fun setCurrentInventorySlotItem(item: Item) {
        inventory.items[inventory.activeSlot] = item.toInventoryItem()
    }

    private fun drawItem(spriteBatch: SpriteBatch, x: Float, y: Float, handAnim: Float) {
        val item = inventory.activeItem.item.takeIf { !it.isNone() } ?: return
        val itemSprite = item.sprite
        val isSmallSprite = !item.isTool() || item.isShears()
        val originalWidth = itemSprite.width
        val originalHeight = itemSprite.height
        val handLength = sprite.hand.height

        if (isSmallSprite) {
            itemSprite.setSize(SMALL_ITEM_SIZE, SMALL_ITEM_SIZE)
        }

        val spriteOrigin = item.params.inHandSpriteOrigin
        val handMultiplier = -direction.basis
        val xOffset = (-1 + direction.index) * itemSprite.width + SMALL_ITEM_SIZE / 2 +
                handMultiplier * itemSprite.width * spriteOrigin.x
        val yOffset = if (!isSmallSprite) - itemSprite.height / 2 else 0f

        val rotate = handAnim + HAND_ITEM_ANGLE_DEG

        if (item.isTool()) {
            itemSprite.rotate90(looksLeft())
        }

        val itemX = x + handLength * MathUtils.sin(handMultiplier * handAnim * MathUtils.degRad) + xOffset
        val itemY = y + handLength * MathUtils.cos(handMultiplier * handAnim * MathUtils.degRad) + yOffset

        if (looksLeft()) {
            itemSprite.setFlip(!item.isTool(), itemSprite.isFlipY)
            itemSprite.applyOrigin(spriteOrigin.getFlipped(flipX = true, flipY = false))
        } else {
            itemSprite.setFlip(item.isTool(), itemSprite.isFlipY)
            itemSprite.applyOrigin(spriteOrigin)
        }

        itemSprite.rotation = -handMultiplier * rotate
        itemSprite.setPosition(itemX, itemY)
        itemSprite.draw(spriteBatch)


        // dont forget to reset
        itemSprite.setFlip(false, itemSprite.isFlipY)
        itemSprite.rotation = 0f
        itemSprite.setOriginCenter()
        itemSprite.setSize(originalWidth, originalHeight)
        if (item.isTool()) {
            itemSprite.rotate90(looksRight())
        }
    }

    private fun getRightHandAnim(delta: Float): Float {
        hitAnim -= hitAnimDelta * delta

        if (hitAnim !in HIT_ANIMATION_RANGE) {
            if (isHitting) {
                hitAnim = MathUtils.clamp(hitAnim, HIT_ANIMATION_RANGE.start, HIT_ANIMATION_RANGE.endInclusive)
                hitAnimDelta = -hitAnimDelta
            } else {
                hitAnimDelta = ANIMATION_SPEED
            }
        }

        if (!isHitting) {
            if (hitAnim < hitAnimDelta * delta) {
                hitAnim = 0f
                hitAnimDelta = 0f
                return -anim
            }
        }

        return hitAnim
    }

    enum class ControlMode {
        WALK,
        CURSOR
    }

    companion object {
        const val HOTBAR_SIZE = 9
        const val INVENTORY_SIZE = 36

        const val MAX_HEALTH = 20

        private const val WIDTH = 4f
        private const val HEIGHT = 30f

        private const val SPEED = 69.072f
        private const val JUMP_VELOCITY = -133.332f

        private val HIT_ANIMATION_RANGE = 30f..90f

        private val SMALL_ITEM_SIZE = 8f
        private val HAND_ITEM_ANGLE_DEG = 30f
    }
}