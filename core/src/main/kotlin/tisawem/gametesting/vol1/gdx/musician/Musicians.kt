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
import tisawem.gametesting.vol1.midi.Instrument
import tisawem.gametesting.vol1.midi.InstrumentStandard
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration

enum class GeneralMusicians (val instrument: Instrument,val musician:( TimeBasedSequence,Score,()-> Duration)-> Musician){
 RENKWEEN(Instrument(0,0,0),{ t, s, p-> Renkween(t,s,p) }),

    XUANKONG(Instrument(0,0,1),{ t , s , p->
        XuanKong(
              t,
              s,
              p
          )
    });


}
enum class PercussionMusicians (val instrument: Instrument,val musician:( TimeBasedSequence,Score,()-> Duration)-> Musician){
QINGXUE(InstrumentStandard.defaultPercussion,{t,s,p-> QingXue(t,s,p) })

 ;


}
