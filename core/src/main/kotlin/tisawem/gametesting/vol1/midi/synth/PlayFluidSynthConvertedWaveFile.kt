package tisawem.gametesting.vol1.midi.synth

import org.jjazz.fluidsynthjava.api.FluidSynthJava

import java.io.File
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import javax.sound.sampled.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * A MIDI player implementation that converts MIDI files to WAV format using [FluidSynthJava]
 * and plays the converted audio file. This class implements the [MidiPlayer] interface
 * and provides functionality for playing, stopping, and tracking the position of MIDI playback.
 *
 * The conversion process uses FluidSynth to generate a temporary WAV file from the provided MIDI file.
 * The temporary WAV file is automatically deleted when the program exits. Playback is handled in a
 * dedicated daemon thread to avoid blocking the main thread.
 *
 * The class supports optional callbacks for notifying when playback starts and finishes. It ensures
 * that the player can only be used once per instance, and attempts to reuse the player will result
 * in an exception.
 *
 * The polling interval for monitoring playback status is defined as a constant value of 2000 milliseconds.
 *
 * This implementation relies on the system's audio capabilities and may throw exceptions if the required
 * audio line is not supported or if errors occur during playback.
 *
 * Exceptions during playback are handled internally, and an error dialog is displayed to notify the user.
 * Resources such as audio streams and clips are properly cleaned up after playback stops or encounters an error.
 */
class PlayFluidSynthConvertedWaveFile(
    override val midiFile: File,
    override val readyCallback: (() -> Unit)?,
    override val finishCallback: (() -> Unit)?
) : MidiPlayer {
    companion object {
        private const val POLLING_INTERVAL = 2000L // 轮询间隔
    }

    private val fluidSynthConvertedWavFile: File by lazy {
        val tempFile = File.createTempFile(midiFile.nameWithoutExtension + "_(Converted)", ".wav")
        tempFile.deleteOnExit()
        FluidSynthJava().apply {
            open(false)
            loadSoundFont(File(ConfigItem.MIDIOutputDevice.load()))
            generateWavFile(midiFile, tempFile)
            close()
        }
        tempFile
    }

    private var clip: Clip? = null
    private var audioInputStream: AudioInputStream? = null
    private var playbackThread: Thread? = null
    private val playLock = Object()
    private var isPlaying = false
    private var hasBeenPlayed = false

    override fun play() {
        synchronized(playLock) {
            if (hasBeenPlayed) {
                ExceptionDialog(IllegalStateException(),true,"$this 实例，只允许播放一次。一旦停止，实例即作废。")

                return
            }

            if (isPlaying) {
                return
            }

            hasBeenPlayed = true
            isPlaying = true

            // 使用单独的线程播放，避免阻塞调用线程
            playbackThread = Thread {
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(fluidSynthConvertedWavFile)

                    val format = audioInputStream?.format
                    val info = DataLine.Info(Clip::class.java, format)

                    if (!AudioSystem.isLineSupported(info)) {

                        ExceptionDialog(RuntimeException(),true,"Line not supported: $info")
                        synchronized(playLock) {
                            cleanup()
                        }
                        return@Thread
                    }

                    clip = AudioSystem.getLine(info) as Clip
                    clip?.apply {
                        open(audioInputStream)
                        addLineListener { event ->
                            if (event.type == LineEvent.Type.STOP) {
                                synchronized(playLock) {
                                    if (isPlaying) {
                                        cleanup()
                                    }
                                }
                            }
                        }
                        start()

                        // 通知播放已开始
                        readyCallback?.invoke()

                        // 等待播放完成
                        while (isPlaying && this.isRunning) {
                            try {
                                Thread.sleep(POLLING_INTERVAL)
                            } catch (_: InterruptedException) {
                                // 继续检查循环条件
                            }
                        }
                    }
                } catch (e: Exception) {
                    ExceptionDialog(e,true,"未知错误。")
                    synchronized(playLock) {
                        if (isPlaying) {
                            cleanup()
                        }
                    }
                }
            }
            playbackThread?.name = "MIDI-Playback-Thread"
            playbackThread?.isDaemon = true
            playbackThread?.start()
        }
    }

    override fun stop() {
        synchronized(playLock) {
            if (isPlaying) {
                cleanup()
            }
        }
    }

    override fun getPosition(): Duration? {
        synchronized(playLock) {
            if (!isPlaying || clip == null) {
                return null
            }
            return (clip?.microsecondPosition ?: 0).toDuration(DurationUnit.MICROSECONDS)
        }
    }

    private fun cleanup() {
        isPlaying = false

        try {
            clip?.apply {
                stop()
                close()
            }
            clip = null

            audioInputStream?.close()
            audioInputStream = null
        } finally {
            finishCallback?.invoke()
        }
    }
}
