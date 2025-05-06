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

import tisawem.gametesting.vol1.config.ConfigItem
import java.io.File
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import kotlin.collections.forEach

/**
 * A singleton object responsible for managing MIDI output devices and providing playback functionality.
 *
 * This object provides methods to retrieve available MIDI output devices, determine the preferred output device,
 * and select an appropriate player implementation based on the availability of the preferred device.
 *
 * The `getAvailableMIDIOutputDevices` method filters out specific devices such as "Gervill"、"Real Time Sequencer",
 * "Microsoft MIDI Mapper", and "Microsoft GS Wavetable Synth". Only devices capable of receiving MIDI data
 * are included in the resulting map.
 *
 * The `getPreferredOutputDevice` method retrieves the preferred MIDI output device based on the configuration
 * stored in [ConfigItem.MIDIOutputDevice]. If the configured device is unavailable, it returns null.
 *
 * The `getPlayer` method determines the appropriate playback implementation. If a preferred MIDI output device
 * is available, it uses [SendSequenceToMidiDevice] to send MIDI sequences directly to the device. Otherwise,
 * it falls back to [PlayFluidSynthConvertedWaveFile] for playback using a converted audio file.
 *
 * This object ensures that MIDI playback is handled efficiently and provides mechanisms for selecting
 * the most suitable playback strategy based on the system's MIDI device configuration.
 */
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

    fun getPreferredOutputDevice()=getAvailableMIDIOutputDevices().find { it.name==ConfigItem.MIDIOutputDevice.load() }


    /**
     * 根据设备信息选择合适的播放器实现
     */
    fun getPlayer(
        midiFile: File,
        readyCallback: (() -> Unit)? = null,
        finishCallback: (() -> Unit)? = null
    )= if ( getPreferredOutputDevice() != null) {
        SendSequenceToMidiDevice(midiFile,readyCallback,finishCallback)
    } else if (ConfigItem.UsingGervillNotFluidSynthJava.load().toBoolean()){
        GervillMidiPlayer(midiFile,readyCallback,finishCallback)
    }
    else {
        PlayFluidSynthConvertedWaveFile(midiFile,readyCallback,finishCallback)
    }
}


