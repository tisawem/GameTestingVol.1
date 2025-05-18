package tisawem.gametesting.vol1.gdx.musician

import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.Instrument
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

enum class GeneralMusicians (val instrument: Instrument,val musician:( TimeBasedSequence,Score,()-> Duration)-> Musician){
 RENKWEEN(Instrument(0,0,0),{ timedBasedSequence, score, getPosition-> Renkween(timedBasedSequence,score,getPosition) });



}
enum class PercussionMusicians (val musician:( TimeBasedSequence,Score,()-> Duration)-> Musician){


 ;


}
