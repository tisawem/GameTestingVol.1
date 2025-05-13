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

import org.wysko.kmidi.midi.TimedArc
import org.wysko.kmidi.midi.event.ControlChangeEvent
import org.wysko.kmidi.midi.event.Event
import org.wysko.kmidi.midi.event.ProgramEvent


/**
 * 乐器变更基础接口
 */
sealed interface Instrument {
    val tick: Int
    val program: ProgramEvent
}

/**
 * 普通乐器变更
 */
data class GeneralInstrument(
    val msb: ControlChangeEvent,
    val lsb: ControlChangeEvent,
    override val program: ProgramEvent,
    override val tick: Int
) : Instrument

/**
 * 打击乐器变更
 */
data class PercussionInstrument(
    val lsb: ControlChangeEvent,
    override val program: ProgramEvent,
    override val tick: Int
) : Instrument

/**
 * 表示演奏席位要演奏的音乐
 */
sealed interface SeatPerformMusic {
    /**
     * 要演奏的音符列表
     */
    val arcs: List<TimedArc>

 val events: List<Event>

    /**
     * 非打击乐器使用的轨道
     */
    data class General(
        override val arcs: List<TimedArc>,
        val instrumentChanges: List<GeneralInstrument>,
        override val events: List<Event>
    ) : SeatPerformMusic

    /**
     * 打击乐器专用轨道
     */
    data class Percussion(
        override val arcs: List<TimedArc>,
        val instrumentChanges: List<PercussionInstrument>,
        override val events: List<Event>
    ) : SeatPerformMusic
}
