/**
 *     GameTestingVol.1
 *     Copyright (C) 2020-2025 Tisawem東北項目
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package tisawem.gametesting.vol1

import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.Score
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
