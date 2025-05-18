package tisawem.gametesting.vol1.gdx.musician

import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.InstrumentChange
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

/**
 * 能获得席位的各种各样函数
 */
enum class AllocateMusicianFunctions(val allocateGeneral:  ( InstrumentChange)->Function3<TimeBasedSequence, Score, Function0<Duration>, Musician>?,val allocatePercussion:(InstrumentChange)->Function3<TimeBasedSequence, Score, Function0<Duration>, Musician>?){
    RANDOM({
        GeneralMusicians.entries.randomOrNull()?.musician
    },{
        PercussionMusicians.entries.randomOrNull()?.musician
    })

;



}

