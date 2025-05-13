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

package tisawem.gametesting.vol1.lwjgl3.midi.player

import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
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
            loadAllInstruments( MidiSystem.getSoundbank(File(DesktopConfig.MIDIOutputDevice.load())))
        }
    }catch (e: Throwable){
        ExceptionDialog(e, true, "Gervill合成器初始化错误。")
        null
    }

    override val midiDevice=synthesizer


}
