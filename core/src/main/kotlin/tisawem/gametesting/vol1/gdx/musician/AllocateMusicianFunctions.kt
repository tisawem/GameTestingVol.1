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

import org.wysko.kmidi.midi.TimeBasedSequence
import tisawem.gametesting.vol1.midi.InstrumentChange
import tisawem.gametesting.vol1.midi.InstrumentStandard
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

/**
 * 能获得席位的各种各样函数
 */
enum class AllocateMusicianFunctions(
    val allocate: (InstrumentChange) -> Function3<TimeBasedSequence, Score, Function0<Duration>, Musician>?
) {

    /**
     * 随机分配
     *
     * 根据MSB是否为打击乐器轨道，随机分配[GeneralMusicians]，或者[PercussionMusicians]
     *
     * 如果没有任何[PercussionMusicians]，就会分配一个[GeneralMusicians]
     */

    RANDOM({

        when(it.msb.channel){

            InstrumentStandard.PERCUSSION_CHANNEL->    PercussionMusicians.entries.randomOrNull()?.musician ?: GeneralMusicians.entries.randomOrNull()?.musician
else ->  GeneralMusicians.entries.randomOrNull()?.musician

        }




    })
    ;


}

