package tisawem.gametesting.vol1.gdx.screen

import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.gdx.Game
import tisawem.gametesting.vol1.gdx.musician.AllocateMusicianFunctions
import tisawem.gametesting.vol1.gdx.musician.Musician
import tisawem.gametesting.vol1.midi.InstrumentStandard
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Perform(game: Game) : GeneralScreen(game) {
    private val score=game.bridge.score.apply {
        filter { it.arcs.isEmpty() }//过滤掉没有Note事件的轨道
        forEach { it.instrumentChanges.ifEmpty {listOf(InstrumentStandard(it.arcs.first().channel))  }
            AllocateMusicianFunctions.RANDOM.allocate(it.instrumentChanges.first())!!

        }//保证至少有1个乐器变更事件

    }


    private val musicians= ArrayDeque<Musician>()

    private val advancedTime= CoreConfig.ScreenAdvancedTime.load().toIntOrNull()?.toDuration(DurationUnit.MILLISECONDS)?: Duration.ZERO


    init {

    }




}
