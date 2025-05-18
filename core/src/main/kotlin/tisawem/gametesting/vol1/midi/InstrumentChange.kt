package tisawem.gametesting.vol1.midi

import org.wysko.kmidi.midi.event.ControlChangeEvent
import org.wysko.kmidi.midi.event.ProgramEvent
import tisawem.gametesting.vol1.config.CoreConfig

/**
 *  乐器变更
 */
data class  InstrumentChange(
    val msb: ControlChangeEvent,
    val lsb: ControlChangeEvent,
    val program: ProgramEvent,
    val tick: Int
)

