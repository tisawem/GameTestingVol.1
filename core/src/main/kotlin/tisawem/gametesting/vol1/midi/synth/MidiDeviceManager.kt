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

package tisawem.gametesting.vol1.midi.synth

import tisawem.gametesting.vol1.config.Config
import java.io.File
import javax.sound.midi.MidiSystem


object MidiDeviceManager {
    /**
     * 获取可用的 MIDI 输出设备
     *
     * "Real Time Sequencer"、"Microsoft MIDI Mapper"、"Microsoft GS Wavetable Synth"、"Gervill"这类设备会被过滤掉。
     */
    fun getAvailableMIDIOutputDevices()  =
        MidiSystem.getMidiDeviceInfo().filter {
            when (it.name) {
                "Real Time Sequencer", "Microsoft MIDI Mapper", "Microsoft GS Wavetable Synth","Gervill" -> false
                else -> try {
                    // 只有接收 MIDI 数据的设备才加入 map
                    MidiSystem.getMidiDevice(it).maxReceivers != 0
                } catch (_: Throwable) {
                    // 忽略不可用设备
                    false
                }
            }
        }

    fun getPreferredOutputDevice()=getAvailableMIDIOutputDevices().find { it.name==Config.MIDIOutputDevice.load() }


    /**
     * 根据设备信息选择合适的播放器实现
     */
    fun getPlayer(
        midiFile: File,
        readyCallback: (() -> Unit)? = null,
        finishCallback: (() -> Unit)? = null
    )= if ( getPreferredOutputDevice() != null) {
        SendSequenceToMidiDevice(midiFile,readyCallback,finishCallback)
    } else if (Config.UsingGervill.load().toBoolean()){
        GervillMidiPlayer(midiFile,readyCallback,finishCallback)
    }
    else {
        PlayFluidSynthConvertedWaveFile(midiFile,readyCallback,finishCallback)
    }
}


