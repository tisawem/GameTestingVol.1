package tisawem.gametesting.vol1.ui.gdx.seat

import tisawem.gametesting.vol1.midi.GeneralInstrument
import tisawem.gametesting.vol1.midi.Instrument
import tisawem.gametesting.vol1.midi.PercussionInstrument



object AllocateSeat{

    /**
     * @return 演奏席位，如果是null，代表[Seats]没有任何乐器
     */
     fun getRandomSeat(instrument: Instrument): PerformSeat?=   when(instrument){
        is GeneralInstrument -> Seats.entries.map { it.seat }.filterIsInstance<GeneralSeat>().randomOrNull()

        is PercussionInstrument ->  Seats.entries.map { it.seat }.filterIsInstance<PercussionSeat>().randomOrNull()?:Seats.entries.map { it.seat }.filterIsInstance<GeneralSeat>().randomOrNull()//如果没有打击乐席位，就分配普通乐器顶替
    }

}
