package ru.deadsoftware.cavedroid.game.mobs

import ru.deadsoftware.cavedroid.game.GameItemsHolder
import ru.deadsoftware.cavedroid.game.GameScope
import ru.deadsoftware.cavedroid.game.mobs.player.Player
import ru.deadsoftware.cavedroid.game.model.dto.SaveDataDto
import ru.deadsoftware.cavedroid.game.ui.TooltipManager
import ru.deadsoftware.cavedroid.misc.Saveable
import java.util.*
import javax.inject.Inject

@GameScope
class MobsController @Inject constructor(
    gameItemsHolder: GameItemsHolder,
    tooltipManager: TooltipManager,
) : Saveable {

    private val _mobs = LinkedList<Mob>()

    var player: Player = Player(gameItemsHolder, tooltipManager)
        private set

    val mobs: List<Mob>
        get() = _mobs

    fun addMob(mob: Mob) {
        _mobs.add(mob)
    }

    override fun getSaveData(): SaveDataDto.MobsControllerSaveData {
        return SaveDataDto.MobsControllerSaveData(
            version = SAVE_DATA_VERSION,
            mobs = _mobs.map(Mob::getSaveData),
            player = player.getSaveData(),
        )
    }

    companion object {
        private const val SAVE_DATA_VERSION = 1

        private const val TAG = "MobsController"

        fun fromSaveData(
            saveData: SaveDataDto.MobsControllerSaveData,
            gameItemsHolder: GameItemsHolder,
            tooltipManager: TooltipManager
        ): MobsController {
            saveData.verifyVersion(SAVE_DATA_VERSION)

            return MobsController(gameItemsHolder, tooltipManager)
                .apply {
                    _mobs.addAll(saveData.mobs.map { mob -> Mob.fromSaveData(mob) })
                    player = Player.fromSaveData(saveData.player, gameItemsHolder, tooltipManager)
                }
        }
    }
}