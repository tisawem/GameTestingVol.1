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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import org.slf4j.LoggerFactory

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
        private const val POLLING_INTERVAL = 2000L // 轮询间隔
        private const val STOP_TIMEOUT_SECONDS = 5L // stop操作的超时时间（秒）
        private val logger = LoggerFactory.getLogger(GervillMidiPlayer::class.java)
    }

    private var isPlaying = false
    private val stopExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r).apply {
            isDaemon = true
            name = "GervillStopExecutor"
        }
    }

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

    override fun stop() {
        if (!isPlaying) {
            return
        }

        val stopFuture = stopExecutor.submit {
            try {
                // 尝试正常停止
                sequencer?.stop()
                sequencer?.close()
                synthesizer?.close()
            } catch (e: Exception) {
                // 记录错误但继续执行
                logger.error("Error during normal stop: {}", e.message, e)
            }
        }

        try {
            // 等待stop操作完成，最多等待5秒
            stopFuture.get(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            // 超时了，取消任务并强制清理
            logger.warn("Stop operation timed out after {} seconds, forcing cleanup...", STOP_TIMEOUT_SECONDS)
            stopFuture.cancel(true)

            // 强制清理资源
            forceCleanup()
        } catch (e: Exception) {
            logger.error("Error during stop operation: {}", e.message, e)
            forceCleanup()
        } finally {
            isPlaying = false
            finishCallback?.invoke()
        }
    }

    private fun forceCleanup() {
        try {
            // 强制置空引用，让垃圾回收器处理
            sequencer = null
            synthesizer = null
        } catch (_: Exception) {
            // 忽略任何错误
        }
    }

    override fun getMicroSecondPosition(): Long? = if (isPlaying) sequencer?.microsecondPosition else null

    // 清理ExecutorService
    protected fun finalize() {
        stopExecutor.shutdown()
        try {
            if (!stopExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                stopExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            stopExecutor.shutdownNow()
        }
    }
}
