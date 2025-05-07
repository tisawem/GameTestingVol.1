/**
 *     GameTestingVol.1
 *     Copyright (C) 2020-2025 Tisawem東北項目
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package tisawem.gametesting.vol1.midi

import arrow.core.Tuple4
import org.wysko.kmidi.midi.TimedArc
import org.wysko.kmidi.midi.event.ControlChangeEvent
import org.wysko.kmidi.midi.event.Event
import org.wysko.kmidi.midi.event.ProgramEvent

/**
 * 密封类，代表演奏席位要演奏的轨道
 *
 */
/**
 * 乐器变更基础接口
 */
sealed interface InstrumentChange {
    val tick: Int
    val program: ProgramEvent
}

/**
 * 普通乐器变更
 */
data class GeneralInstrumentChange(
    val msb: ControlChangeEvent,
    val lsb: ControlChangeEvent,
    override val program: ProgramEvent,
    override val tick: Int
) : InstrumentChange

/**
 * 打击乐器变更
 */
data class PercussionInstrumentChange(
    val lsb: ControlChangeEvent,
    override val program: ProgramEvent,
    override val tick: Int
) : InstrumentChange

/**
 * 表示演奏席位要演奏的音乐
 */
sealed class SeatPerformMusic {
    /**
     * 要演奏的音符列表
     */
    abstract val arcs: List<TimedArc>

    /**
     * 非打击乐器使用的轨道
     */
    data class GeneralInstrument(
        override val arcs: List<TimedArc>,
        val instrumentChanges: List<GeneralInstrumentChange>
    ) : SeatPerformMusic()

    /**
     * 打击乐器专用轨道
     */
    data class Percussion(
        override val arcs: List<TimedArc>,
        val instrumentChanges: List<PercussionInstrumentChange>
    ) : SeatPerformMusic()
}
