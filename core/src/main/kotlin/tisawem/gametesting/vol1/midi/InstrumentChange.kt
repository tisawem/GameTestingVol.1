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

