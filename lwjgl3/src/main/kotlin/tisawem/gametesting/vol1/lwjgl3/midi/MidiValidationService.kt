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

package tisawem.gametesting.vol1.lwjgl3.midi

import arrow.core.Either
import org.wysko.kmidi.midi.reader.StandardMidiFileReader
import org.wysko.kmidi.midi.reader.readFile
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.file.FileCheckingMethod
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import tisawem.gametesting.vol1.lwjgl3.midi.player.MidiDeviceManager
import java.io.File

/**
 *提供[ProcessedMIDIData]实例（如果MIDI文件不符合要求，或者代码意外地抛出错误。可能为null），以及问题列表（空列表代表没问题）
 */
object MidiValidationService {
    /**
     * 返回错误列表，代表不能播放的原因，空列表表示验证通过
     */
    fun validate(): List<String> {
        val errors = ArrayList<String>()
        try {

            when (val midiOrReason=
                FileCheckingMethod.MIDIFile.method(File(DesktopConfig.MIDIFile.load()))
            ) {
                is Either.Left<*> -> errors.add(midiOrReason.leftOrNull().toString())//代表了路径空，文件无效，文件没有Note事件
                is Either.Right<*> -> {}//到这里已经是有效文件了
            }


            //如果不是MIDI OUT设备名称，则尝试视为SoundFont文件路径打开
            if (MidiDeviceManager.getPreferredOutputDevice() == null) {
                when (FileCheckingMethod.SoundFont.method(File(DesktopConfig.MIDIOutputDevice.load()))) {
                    is Either.Left<*> ->errors.add(Messages.getMessages("Remind_User_Set_Output_MIDI_Device"))
                    is Either.Right<*> -> {}
                }
            }


        } catch (e: Throwable) {
            ExceptionDialog(e, true, "未知错误。")
        }
        return errors
    }

    /**
     *  如果验证通过，创建并返回MidiEventProcess实例，否则返回null
     */
    fun createMidiEventProcess()=  try {

        val file = FileCheckingMethod.MIDIFile.method(File(DesktopConfig.MIDIFile.load())).getOrNull() ?: return null

        ProcessedMIDIData(StandardMidiFileReader().readFile(file))
    } catch (e: Throwable) {
        ExceptionDialog(e,true,"未知原因")
        null
    }
}
