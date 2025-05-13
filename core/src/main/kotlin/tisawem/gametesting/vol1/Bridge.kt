package tisawem.gametesting.vol1

import org.wysko.kmidi.midi.TimeBasedSequence
import org.wysko.kmidi.midi.event.Event
import tisawem.gametesting.vol1.midi.SeatPerformMusic
import kotlin.time.Duration

interface Bridge {
    /**
     * 获取基于时间的MIDI序列
     */

    val timedBaseSequence: TimeBasedSequence
    /**
     * 获取当前播放进度
     */
    fun getPosition():  Duration?

    /**
     * 演奏轨道
     */
    val generalInstrumentMusic :ArrayDeque<SeatPerformMusic.General>
    val percussionMusic : ArrayDeque<SeatPerformMusic.Percussion>
}
