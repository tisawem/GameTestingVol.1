package tisawem.gametesting.vol1.midi.synth

import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import java.io.File
import javax.sound.midi.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * A class responsible for sending a MIDI sequence to a MIDI output device for playback.
 * Implements the [MidiPlayer] interface and provides functionality to play, stop, and monitor the playback of a MIDI file.
 * Ensures proper resource management by cleaning up MIDI devices and sequencers after use.
 *
 * The class supports optional callbacks for notifying when playback is ready to start and when it has finished.
 * It ensures thread safety during playback operations using synchronization mechanisms.
 *
 * The MIDI output device is determined lazily based on the preferred device specified in the configuration.
 * If the preferred device is unavailable or cannot be opened, an error dialog is displayed, and playback is aborted.
 *
 * Playback is handled in a dedicated daemon thread to avoid blocking the main application thread.
 * The playback state is continuously monitored, and resources are released appropriately when playback stops or encounters an error.
 *
 * Note: This player is designed for single-use only. Once playback has started, the instance cannot be reused.
 */
class SendSequenceToMidiDevice(
    override val midiFile: File,
    override val readyCallback: (() -> Unit)?,
    override val finishCallback: (() -> Unit)?
) : MidiPlayer {
    private var sequencer: Sequencer? = null
    private var midiOutDevice: MidiDevice? = null
    private var synthesizer: Synthesizer? = null
    private var isPlaying = false
    private var hasBeenPlayed = false
    private val playLock = Object()
    private var playbackThread: Thread? = null

    companion object {
        private const val POLLING_INTERVAL = 2000L // 轮询间隔
    }

    private val midiDevice: MidiDevice? by lazy {
        try {
            MidiDeviceManager.getPreferredOutputDevice()?.let {
                MidiSystem.getMidiDevice(it).also { device ->
                   if (!device.isOpen){
                       device.open()
                   }
                }
            }
        } catch (e: MidiUnavailableException) {
            ExceptionDialog(e, true, "无法打开MIDI输出设备")
            null
        }
    }

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

            playbackThread = Thread {
                try {
                    MidiSystem.getSequencer(midiDevice == null).apply {
                        sequence = MidiSystem.getSequence(midiFile)
                        midiDevice?.let {
                            transmitter.receiver = it.receiver
                        }
                        open()
                        start()

                        // 调用准备就绪回调
                        readyCallback?.invoke()
                    }


                    // 等待播放完成
                    while (isPlaying && sequencer?.isRunning == true) {
                        try {
                            Thread.sleep(POLLING_INTERVAL)
                        } catch (_: InterruptedException) {
                            // 继续检查循环条件
                        }
                    }
                } catch (e: Exception) {
                    ExceptionDialog(e, true)
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

    override fun getPosition(): Duration? = synchronized(playLock) {
        val currentSequencer = sequencer
        if (!isPlaying || currentSequencer == null) {
            null
        } else {
            currentSequencer.microsecondPosition.toDuration(DurationUnit.MICROSECONDS)
        }
    }

    private fun cleanup() {
        isPlaying = false

        try {
            sequencer?.apply {
                stop()
                close()
            }
            sequencer = null

            midiOutDevice?.apply {
                close()
            }
            midiOutDevice = null

            synthesizer?.apply {
                close()
            }
            synthesizer = null
        } finally {
            finishCallback?.invoke()
        }
    }
}
