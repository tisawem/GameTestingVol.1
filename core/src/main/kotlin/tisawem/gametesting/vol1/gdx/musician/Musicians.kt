package tisawem.gametesting.vol1.gdx.musician

import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

enum class GeneralMusicians (val musician:( TimeBasedSequence,Score,()-> Duration)-> Musician){
 RENKWEEN({timedBasedSequence,score,getPosition-> Renkween(timedBasedSequence,score,getPosition) });



}
enum class PercussionMusicians (val musician:( TimeBasedSequence,Score,()-> Duration)-> Musician){


 ;


}
