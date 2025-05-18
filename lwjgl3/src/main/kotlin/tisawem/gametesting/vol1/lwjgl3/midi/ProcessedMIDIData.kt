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

package tisawem.gametesting.vol1.lwjgl3.midi

import arrow.core.Tuple4
import org.wysko.kmidi.midi.StandardMidiFile
import org.wysko.kmidi.midi.TimeBasedSequence.Companion.toTimeBasedSequence
import org.wysko.kmidi.midi.event.*
import tisawem.gametesting.vol1.lwjgl3.midi.DefaultInstrument.Companion.LSB_CONTROLLER
import tisawem.gametesting.vol1.lwjgl3.midi.DefaultInstrument.Companion.MSB_CONTROLLER
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import tisawem.gametesting.vol1.midi.InstrumentChange
import tisawem.gametesting.vol1.midi.Score

/**
 * 对[StandardMidiFile]进行筛分的类，包含筛分后的各类数据
 *
 * 已适配MIDI Format 0的MIDI文件，它把所有事件放在一个轨道上。
 *
 *@throws
 */
class ProcessedMIDIData(kStdMidiFile: StandardMidiFile) {
    init {
        if (!kStdMidiFile.tracks.any { it.arcs.isNotEmpty() })
            ExceptionDialog(IllegalArgumentException(), false, "构造器传入的 $kStdMidiFile 实例没有音符。")

        initSeatsMusic()//初始化各席位的乐谱
    }





    /**
     * 按照[MidiEvent.channel]值，重新归类所有的[MidiEvent]，组成新的[StandardMidiFile.Track]集合
     *
     * 它不包含任何[MetaEvent]
     */
    private val channelSortedMidiEventsTracks = kStdMidiFile.tracks
        .flatMap { track -> track.events.filterIsInstance<MidiEvent>() }//汇集所有的MidiEvent
        .groupBy { it.channel }//组成Map<Channel值, MidiEvent列表>
        .filter { (_, events) -> events.isNotEmpty() }//筛掉没有事件的通道
        .toSortedMap() // 按通道号排序
        .map { (_, events) -> StandardMidiFile.Track(events.sortedBy { it.tick }) }//按顺序转成List<List<Event>>，且根据tick值排序List<Event>


    /**
     * 基于时间轴的序列
     */


    /**
     * Extracts instrument change events from a sequence of MIDI events.
     *
     * Tracks MSB, LSB, and Program Change events to determine instrument changes.
     *
     * @param events Source MIDI events
     * @param msb Default MSB controller event
     * @param lsb Default LSB controller event
     * @param programChange Default program change event
     * @return Collection of instrument change events with tick.
     */
    private tailrec fun extractInstrumentChanges(
        events: ArrayDeque<Event>,
        msb: ControlChangeEvent,
        lsb: ControlChangeEvent,
        programChange: ProgramEvent,
        result: ArrayDeque<Tuple4<ControlChangeEvent, ControlChangeEvent, ProgramEvent, Int>> = ArrayDeque()
    ): ArrayDeque<Tuple4<ControlChangeEvent, ControlChangeEvent, ProgramEvent, Int>> {
        // Base case: no more events to process
        if (events.isEmpty()) return result

        // Process current event
        when (val currentEvent = events.removeFirst()) {
            is ProgramEvent -> {
                result.add(Tuple4(msb, lsb, currentEvent, currentEvent.tick))
            }

            is ControlChangeEvent -> {
                when (currentEvent.controller) {
                    MSB_CONTROLLER -> {
                        result.add(Tuple4(currentEvent, lsb, programChange, currentEvent.tick))
                    }

                    LSB_CONTROLLER -> {
                        result.add(Tuple4(msb, currentEvent, programChange, currentEvent.tick))
                    }
                    // Ignore other controller types
                }
            }

            else -> {}
        }

        // Continue processing with updated instrument state if we added a new result
        return if (result.isNotEmpty()) {
            val lastChange = result.last()
            extractInstrumentChanges(events, lastChange.first, lastChange.second, lastChange.third, result)
        } else {
            extractInstrumentChanges(events, msb, lsb, programChange, result)
        }
    }

    /**
     * Process each channel track into appropriate music collections.
     */
    private fun initSeatsMusic() =channelSortedMidiEventsTracks.forEach { track ->
        val defaultInstrument = DefaultInstrument(0,(track.events.first() as MidiEvent).channel)

        // Extract instrument changes
        val instrumentChanges: List<InstrumentChange> = extractInstrumentChanges(
            events = ArrayDeque(track.events),
            msb = defaultInstrument.defaultMSBEvent,
            lsb = defaultInstrument.defaultLSBEvent,
            programChange = defaultInstrument.defaultProgramEvent
        ).map { (msb, lsb, programChange, tick) ->
            InstrumentChange(msb, lsb, programChange, tick)
        }

        // Create normal instrument music entry
        scores.add(
            Score( timeBasedSequence.convertArcsToTimedArcs(track.arcs),
                track.events,
                instrumentChanges
            )
        )
    }


    /*
    公共访问权限的API
     */

    val timeBasedSequence = kStdMidiFile.toTimeBasedSequence()


    /**
     * 各个乐谱
     */
    val scores = ArrayDeque<Score>()



}
