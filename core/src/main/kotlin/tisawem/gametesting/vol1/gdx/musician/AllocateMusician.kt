package tisawem.gametesting.vol1.gdx.musician

import tisawem.gametesting.vol1.midi.GeneralInstrument
import tisawem.gametesting.vol1.midi.Instrument
import tisawem.gametesting.vol1.midi.PercussionInstrument



object AllocateMusician{

    /**
     * @return 演奏席位，如果是null，代表[Musicians]没有任何乐器
     */
     fun getRandomSeat(instrument: Instrument): Musician?=   when(instrument){
        is GeneralInstrument -> Musicians.entries.map { it.musician }.filterIsInstance<General>().randomOrNull()

        is PercussionInstrument ->  Musicians.entries.map { it.musician }.filterIsInstance<Percussion>().randomOrNull()?: Musicians.entries.map { it.musician }.filterIsInstance<General>().randomOrNull()//如果没有打击乐席位，就分配普通乐器顶替
    }

}
