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

    fun create(readyCallBack:(()->Unit)?=null,finishCallBack:(()->Unit)?=null)

    /**
     * 一对播放和停止的函数
     */
    fun  play ()

    fun stop()

    /**
     * 获取当前播放进度
     *
     * null 就是没播放
     */
    fun getPosition():  Duration?

    /**
     * 演奏轨道
     *
     * 三个列表不得为空，平台端好好处理一下。
     */
    val score :ArrayDeque<Score >


}
