package tisawem.gametesting.vol1.midi.synth

import tisawem.gametesting.vol1.config.Config
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import java.io.File
import javax.sound.midi.*

class GervillMidiPlayer(
    override val midiFile: File,
    override val readyCallback: (() -> Unit)? = null,
    override val finishCallback: (() -> Unit)? = null
) : SendSequenceToMidiDevice(midiFile,readyCallback,finishCallback) {



    private var synthesizer: Synthesizer? = try {
        MidiSystem.getSynthesizer().apply {
            unloadAllInstruments(defaultSoundbank)
            loadAllInstruments( MidiSystem.getSoundbank(File(Config.MIDIOutputDevice.load())))
        }
    }catch (e: Throwable){
        ExceptionDialog(e,true,"Gervill合成器初始化错误。")
        null
    }

    override val midiDevice=synthesizer


}
