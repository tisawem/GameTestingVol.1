package tisawem.gametesting.vol1.midi.synth

import org.jjazz.fluidsynthjava.api.FluidSynthJava

import java.io.File
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import java.lang.UnsupportedOperationException
import javax.sound.sampled.*

class PlayFluidSynthConvertedWaveFile(
    override val midiFile: File,
    override val readyCallback: (() -> Unit)?,
    override val finishCallback: (() -> Unit)?
) : MidiPlayer {

    private val wavFile: File by lazy {
        File.createTempFile(midiFile.nameWithoutExtension + "_(Converted)", ".wav").let {
            it.deleteOnExit()
            FluidSynthJava().apply {
                open(false)
                loadSoundFont(File(ConfigItem.MIDIOutputDevice.load()))
                generateWavFile(midiFile, it)
                close()
            }
            it
        }
    }

    private var clip: Clip? = null
    private var audioInputStream: AudioInputStream? = null
    private var isPlaying = false


    override fun play() {
        if (isPlaying) {
            ExceptionDialog(IllegalStateException(), true, "$this\n\n该实例已经在播放了，或者合成器初始化错误。")
            return
        }

        Thread {
            try {
                // 确保 WAV 文件已创建（通过访问 wavFile 触发 lazy 初始化）
                val waveFileToPlay = wavFile

                audioInputStream = AudioSystem.getAudioInputStream(waveFileToPlay)
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
                ExceptionDialog(e, true, "播放器崩了，或者回调函数出错。")

                close()
                finishCallback?.invoke()
            }
        }.start()
    }

    override fun stop() =if (isPlaying)  clip?.stop()?: Unit else Unit

    override fun getMicroSecondPosition(): Long? = if (isPlaying) clip?.microsecondPosition else null

    private fun close() = try {
            clip?.close()
            audioInputStream?.close()
            clip = null
            audioInputStream = null
        } catch (_: Throwable) { }
}
