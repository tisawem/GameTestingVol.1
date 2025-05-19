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

import org.jjazz.fluidsynthjava.api.FluidSynthJava

import java.io.File
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import tisawem.gametesting.vol1.midi.synth.MidiPlayer
import java.util.concurrent.FutureTask
import javax.sound.sampled.*
class PlayFluidSynthConvertedWaveFile(
    override val midiFile: File,
    override var readyCallback: (() -> Unit)?,
    override var finishCallback: (() -> Unit)?
) : MidiPlayer {

    // 使用FutureTask来异步生成WAV文件
    private val wavFile: FutureTask<File> = FutureTask<File> {
        File.createTempFile(midiFile.nameWithoutExtension + "_(Converted)", ".wav").let {
            it.deleteOnExit()
            FluidSynthJava().apply {
                open(false)
                loadSoundFont(File(DesktopConfig.MIDIOutputDevice.load()))
                generateWavFile(midiFile, it)
                close()
            }
            it
        }
    }

    // 在构造函数中就启动线程生成WAV文件
    init {
        Thread(wavFile, "Wav-Generator-${midiFile.nameWithoutExtension}").apply {
            isDaemon = true  // 设为守护线程，不阻止JVM退出
            start()
        }
    }

    private var clip: Clip? = null
    private var audioInputStream: AudioInputStream? = null
    private var isPlaying = false

    override fun play() {
        if (isPlaying) {
            ExceptionDialog(IllegalStateException(), true, "$this\n\n该实例已经播放了。")
            return
        }

        Thread {
            try {
                // 等待WAV文件生成完成并获取结果
                // 如果生成过程中有异常，get()会抛出ExecutionException

                audioInputStream = AudioSystem.getAudioInputStream(wavFile.get())
                clip = AudioSystem.getClip()

                clip?.addLineListener { event ->
                    when (event.type) {
                        LineEvent.Type.START -> {
                            isPlaying = true
                            readyCallback?.invoke()
                        }

                        LineEvent.Type.STOP -> {
                            isPlaying = false
                            close()
                            finishCallback?.invoke()
                        }

                        else -> {} // 忽略其他事件
                    }
                }

                clip?.open(audioInputStream)
                clip?.start()

            } catch (e: Exception) {
                close()
                finishCallback?.invoke()
                ExceptionDialog(e, true, """
1、ExecutionException
    生成Wave文件时出现错误

其他错误为未知错误，可能是音频播放出现意外，或者执行回调函数时抛出错误。
                """.trimIndent())


            }
        }.apply {
            name = "WavFile-Player-${midiFile.nameWithoutExtension}"
            isDaemon=true
            start()
        }
    }

    override fun stop() {
        if (isPlaying) clip?.stop()
    }

    override fun getMicroSecondPosition(): Long? = if (isPlaying) clip?.microsecondPosition else null

    private fun close() = try {
        clip?.close()
        audioInputStream?.close()
        clip = null
        audioInputStream = null
    } catch (_: Throwable) { }
}
