/**
 *     GameTestingVol.1
 *     Copyright (C) 2020-2025 Tisawem東北項目
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
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

import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import java.io.File
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Sequencer


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
open class SendSequenceToMidiDevice(
    override val midiFile: File,
    override var readyCallback: (() -> Unit)?,
    override var finishCallback: (() -> Unit)?
) : MidiPlayer {

    companion object {
        private const val POLLING_INTERVAL = 2000L // 轮询间隔
    }

    private var isPlaying = false

     open val midiDevice=try {
        MidiDeviceManager.getPreferredOutputDevice()?.let {
            MidiSystem.getMidiDevice(it)
        }
    } catch (e: MidiUnavailableException) {
         ExceptionDialog(e, true, "无法打开MIDI输出设备")
        null
    }


    private var sequencer: Sequencer? = try {
        // 初始化sequencer和synthesizer
        MidiSystem.getSequencer(midiDevice==null).apply {
            open()
            midiDevice?.let {
                if (!it.isOpen)it.open()
                transmitter.receiver=it.receiver
            }

            sequence = MidiSystem.getSequence(midiFile)


        }
    } catch (e: Exception) {
        stop()
        ExceptionDialog(e, true, "合成器初始化错误。")
        null
    }



    override fun play() {
        if (isPlaying||sequencer==null){
            ExceptionDialog(IllegalStateException(),true,"$this\n\n该实例已经在播放了。")
            return
        }

        Thread {
            try {


                sequencer?.start()
                isPlaying = true
                readyCallback?.invoke()

                while (sequencer?.isRunning == true) {
                    try {
                        Thread.sleep(POLLING_INTERVAL)
                    } catch (_: InterruptedException) {
                    }
                }
                stop()
            } catch (e: Throwable) {
                stop()
                ExceptionDialog(e, true, "Sequencer崩了，或者回调函数出错。")

            }
        }.apply { isDaemon=true }.start()
    }

    override fun stop() = try {
        if ( isPlaying) {
            sequencer?.stop()
            isPlaying = false
            sequencer?.close()
            sequencer = null
            midiDevice?.close()


        }
        Unit
    } catch (_: Exception) {} finally {

        finishCallback?.invoke()
    }

    override fun getMicroSecondPosition(): Long? = if (isPlaying) sequencer?.microsecondPosition else null




}
