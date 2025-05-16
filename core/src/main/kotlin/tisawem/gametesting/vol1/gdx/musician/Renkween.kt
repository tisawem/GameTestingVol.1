package tisawem.gametesting.vol1.gdx.musician

import com.badlogic.gdx.scenes.scene2d.Actor
import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

class Renkween(
    override val timeBasedSequence: TimeBasedSequence,
    override val score: Score.General, override val getPosition: () -> Duration
) : General, Actor(){
    override fun getActor()=this


fun a(){



    score.arcs.first().duration//音符持续时间，Duration
score.arcs.first().startTime//音符开始时间，Duration
    score.arcs.first().endTime//音符结束时间，Duration

    score.arcs.first().note//音高，byte

}







}
