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

package tisawem.gametesting.vol1.lwjgl3.file

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.jjazz.fluidsynthjava.api.FluidSynthJava
import org.wysko.kmidi.midi.reader.StandardMidiFileReader
import org.wysko.kmidi.midi.reader.readFile
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages.getMessages
import java.io.File
import javax.imageio.ImageIO
import javax.sound.midi.MidiSystem

/**
 * 提供一个函数，如果文件有效，返回Right(File)，反之，Left(String)，包含原因。
 *
 * 流程就是：路径是不是空白，路径是不是指向文件，能否解析这个文件，这个文件是否包含必要信息
 * @param method 一个函数，接收一个[File]，返回[Right]（File）或 [Left]（String）
 */
enum class FileCheckingMethod(
    private val notOpenKey: String,
    private val notFileKey: String,
    private val doCheck: (File) -> Either<String, File>
) {
    MIDIFile(
        notOpenKey = "Has_Not_Open_MIDI",
        notFileKey = "Path_Not_Point_To_MIDI",
        doCheck = { file ->

            try {
                MidiSystem.getSequence(file)
                if (StandardMidiFileReader().readFile(file)
                        .tracks
                        .any { it.arcs.isNotEmpty() }
                ) {
                    Right(file)
                } else {
                    Left(getMessages("MIDI_File_Without_Note"))
                }

            } catch (_: Throwable) {
                Left(getMessages("Cannot_Load_MIDI"))
            }

        }

    ),

    SoundFont(
        notOpenKey = "Has_Not_Open_SF2",
        notFileKey = "Path_Not_Point_To_SF2",
        doCheck = { file ->
            // FluidSynthJava implements AutoCloseable，所以可以用 use
            catching("Cannot_Load_SF2") {
                FluidSynthJava().let { synth ->
                    synth.open(false)
                    synth.loadSoundFont(file)
                }
                file
            }
        }
    ),

    Image(
        notOpenKey = "Has_Not_Open_Image",
        notFileKey = "Path_Not_Point_To_Image",
        doCheck = { file ->
            catching("Cannot_Load_Image") {
                requireNotNull(ImageIO.read(file))
                file
            }
        }
    );

    /**
     * 对外暴露的方法，不变的调用签名：
     * 调用方依旧只是 FileCheckingMethod.Xxx.method(file)
     */
    fun method(file: File): Either<String, File> =
        checkFile(file, notOpenKey, notFileKey, doCheck)
}

/**
 * catch + finally 通用写法 —— runCatching + fold
 */
inline fun <R> catching(resultErrKey: String, block: () -> R): Either<String, R> =
    runCatching { block() }
        .fold(
            { Right(it) },
            { Left(getMessages(resultErrKey)) }
        )

/**
 * 通用的 File 前置检查：
 *  1. path.isBlank()
 *  2. !isFile
 *  3. 业务检查 block
 *
 * @param file         待检查的文件
 * @param notOpenKey   路径空白时的多语言 key
 * @param notFileKey   不是文件时的多语言 key
 * @param block        真正的业务检查（在文件存在且路径不空时才执行）
 */
inline fun checkFile(
    file: File,
    notOpenKey: String,
    notFileKey: String,
    block: (File) -> Either<String, File>
): Either<String, File> = when {
    file.path.isBlank() -> Left(getMessages(notOpenKey))
    !file.isFile -> Left(getMessages(notFileKey))
    else -> block(file)
}
