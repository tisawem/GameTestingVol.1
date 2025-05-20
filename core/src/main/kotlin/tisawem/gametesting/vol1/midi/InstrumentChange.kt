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

package tisawem.gametesting.vol1.midi

import org.wysko.kmidi.midi.event.ControlChangeEvent
import org.wysko.kmidi.midi.event.ProgramEvent


data class Instrument(val msb: Byte,
                      val lsb: Byte,
                      val prg: Byte,)

/**
 *  乐器变更
 */
data class  InstrumentChange(
    val msb: ControlChangeEvent,
    val lsb: ControlChangeEvent,
    val program: ProgramEvent,
    val tick: Int
){
    val instrument= Instrument(msb.value,lsb.value,program.program)
}

