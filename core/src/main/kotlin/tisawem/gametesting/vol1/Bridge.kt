package tisawem.gametesting.vol1

import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.Score
import java.util.Properties
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
    val generalInstrumentMusic :ArrayDeque<Score.General>
    val percussionMusic : ArrayDeque<Score.Percussion>

    /**
     * 传入配置文件
     */
    val configProperties: Properties
}
