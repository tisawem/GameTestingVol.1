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

import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import java.io.File
import javax.sound.midi.*

class GervillMidiPlayer(
    override val midiFile: File,
    override var readyCallback: (() -> Unit)? = null,
    override var finishCallback: (() -> Unit)? = null
) : MidiPlayer {

    private var synthesizer = try {
        MidiSystem.getSynthesizer().apply {
            open() // Ensure synthesizer is open before operations
            unloadAllInstruments(defaultSoundbank)
            // Load the custom SoundFont as instructed
            loadAllInstruments(MidiSystem.getSoundbank(File(DesktopConfig.MIDIOutputDevice.load())))
        }
    } catch (e: Throwable) {
        ExceptionDialog(e, true, "Gervill合成器初始化错误。")
        null
    }

    companion object {
        private const val POLLING_INTERVAL = 2000L//轮询间隔
    }

    private var isPlaying = false

    private var sequencer: Sequencer? = try {
        // Initialize sequencer connected to the synthesizer
        MidiSystem.getSequencer(synthesizer == null).apply {
            open()
            synthesizer?.let { synth ->
                if (!synth.isOpen) synth.open() // Double-check synth is open

                // Connect sequencer to synthesizer
                transmitter.receiver = synth.receiver
            }
            sequence = MidiSystem.getSequence(midiFile)
        }
    } catch (e: Exception) {
        ExceptionDialog(e, true, "Gervill合成器初始化错误。")
        null
    }

    override fun play() {
        if (isPlaying || sequencer == null) {
            ExceptionDialog(IllegalStateException(), true, "该实例已经在播放了。")
            return
        }

        Thread {
            try {
                sequencer?.start()
                isPlaying = true
                readyCallback?.invoke()

                // Monitor until sequencer stops running
                while (sequencer?.isRunning == true) {
                    try {
                        Thread.sleep(POLLING_INTERVAL)
                    } catch (_: InterruptedException) {
                        // Interrupted, break out
                        break
                    }
                }
                stop()
            } catch (e: Throwable) {
                ExceptionDialog(e, true, "Sequencer崩了，或者回调函数出错。")
                stop()
            }
        }.apply {
            isDaemon = true
            name = "GervillPlaybackMonitor" // Named thread for easier debugging
        }.start()
    }

    override fun stop() = try {
        if (isPlaying) {
            isPlaying = false
            sequencer?.stop()
            sequencer?.close()
            sequencer = null
            synthesizer?.close()
            synthesizer = null
        }
        Unit
    } catch (e: Exception) {
        // Log exception but don't display to user since we're in cleanup
        e.printStackTrace()
    } finally {
        finishCallback?.invoke()
    }

    override fun getMicroSecondPosition(): Long? = if (isPlaying) sequencer?.microsecondPosition else null
}
