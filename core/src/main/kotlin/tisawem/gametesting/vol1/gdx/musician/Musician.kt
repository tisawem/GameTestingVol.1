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

package tisawem.gametesting.vol1.gdx.musician

import com.badlogic.gdx.scenes.scene2d.Actor
import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

  interface Musician {

    /**
     * 提供一个Actor，在演奏画面上展示
     */
    fun getActor(): Actor


    /*
     * 三个字段，作为主构造函数实现
     */

    /**
     * 基于[Duration]类，而非tick的MIDI轨道，可以靠这个获得任意MIDI事件发生的时间点
     *
     * 注意，提前注册好每个MIDI事件，这里不会去注册MIDI事件
     */
    val timeBasedSequence: TimeBasedSequence

    /**
     *谱子，提供了音高事件，所有MIDI事件，和乐器变更事件
     */
    val score: Score

    /**
     * 获取当前播放进度
     */
    val getPosition: () -> Duration
}
