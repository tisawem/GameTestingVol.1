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

package tisawem.gametesting.vol1.midi.synth

import java.io.File
import kotlin.time.Duration

/**
 * An interface representing a MIDI player capable of handling playback operations for MIDI files.
 * Provides methods to control playback, retrieve the current position, and manage lifecycle callbacks.
 *
 * The [play] method initiates playback of the associated MIDI file. If playback is already in progress,
 * the behavior does nothing.
 *
 * The [stop] method halts playback and releases associated resources. It ensures proper cleanup
 * of system resources such as sequencers, MIDI devices, or synthesizers.
 *
 * The [getMicroSecondPosition] method retrieves the current playback position as a [Duration] object. If playback
 * is not active or the position cannot be determined, it returns null.
 *
 * Callbacks for [readyCallback] and [finishCallback] allow notification when playback is ready to start
 * and when it has completed, respectively. These callbacks are optional and may be null if not provided.
 *
 * Implementations of this interface are responsible for managing thread safety during playback operations
 * and ensuring that resources are properly released after use.
 *
 * Note: Instances of implementations may be designed for single-use only, meaning they cannot be reused
 * after playback has started and stopped.
 */
interface MidiPlayer {

    //主构造函数
    val midiFile: File
    //绑定开始播放，和播放完毕的回调函数
    var readyCallback :(() -> Unit)?

    var finishCallback :(() -> Unit)?


    //播放
    fun play()

    //停止
    fun stop()


    fun getMicroSecondPosition(): Long?

}
