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

package tisawem.gametesting.vol1.file

import arrow.core.Either
import arrow.core.Either.*
import org.jjazz.fluidsynthjava.api.FluidSynthJava
import org.wysko.kmidi.midi.reader.StandardMidiFileReader
import org.wysko.kmidi.midi.reader.readFile
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import java.io.File
import javax.sound.midi.MidiSystem

/**
 * 提供一个函数，如果文件有效，返回Right(File)，反之，Left(String)，包含原因。
 *
 * 流程就是：路径是不是空白，路径是不是指向文件，能否解析这个文件，这个文件是否包含必要信息
 * @see  FileLoader.loadingFileFromJFileChooser
 * @param method 一个函数，接收一个[java.io.File]，返回[Right]（File）或 [Left]（String）
 */
enum class FileCheckingMethod(val method: (File) -> Either<String, File>) {
    MIDIFile({ it ->
        try {
            if (it.path.isBlank()) {
                Left(getMessages("MIDI_File_Path_Is_Blank"))
            } else if (!it.isFile) {
                Left(getMessages("MIDI_File_Path_Not_Point_to_File"))
            } else {
                MidiSystem.getSequence(it)//是不是javax.sound.midi承认的序列


                //是不是kmidi承认的MIDI文件，若是，检查是否有Arc事件。

                if (StandardMidiFileReader().readFile(it).tracks.any { it.arcs.isNotEmpty() }) Right(it) else Left(
                    getMessages("This_MIDI_File_Without_Note")
                )//有，返回文件，否则提示该文件无音符。

            }

        } catch (_: Throwable) {
            Left(getMessages("Cannot_Load_This_MIDI_File"))
        }
    }),
    SoundFont({
        if (it.path.isBlank()) {
            Left(getMessages("SoundFont_File_Path_Is_Blank"))
        }
        if (!it.isFile) {
            Left(getMessages("SoundFont_File_Path_Not_Point_to_File"))
        }
        val instance = FluidSynthJava()//创建FluidSynthJava实例
        try {
            instance.open(false)
            instance.unloadSoundfont(instance.loadSoundFont(it))//读取（会返回SoundFont ID）再关闭
            Right(it)
        } catch (_: Throwable) {
            Left(getMessages("Cannot_Load_This_SoundFont_File"))
        } finally {
//关闭实例
            instance.close()
        }
    })

}
