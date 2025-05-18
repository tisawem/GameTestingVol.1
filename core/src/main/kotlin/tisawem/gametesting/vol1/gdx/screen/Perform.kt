package tisawem.gametesting.vol1.gdx.screen

import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.gdx.Game
import tisawem.gametesting.vol1.gdx.musician.General
import tisawem.gametesting.vol1.gdx.musician.GeneralMusicians
import tisawem.gametesting.vol1.gdx.musician.Musician
import tisawem.gametesting.vol1.gdx.musician.Percussion
import tisawem.gametesting.vol1.gdx.musician.PercussionMusicians
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Perform(game: Game) : GeneralScreen(game) {

    private val musicians= ArrayDeque<Musician>()

    private val advancedTime= CoreConfig.ScreenAdvancedTime.load().toIntOrNull()?.toDuration(DurationUnit.MILLISECONDS)?: Duration.ZERO


    init {
       game.bridge.score.forEach{


       }
    }




}
