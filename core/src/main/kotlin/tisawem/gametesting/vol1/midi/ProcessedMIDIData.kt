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

package tisawem.gametesting.vol1.midi

import arrow.core.Tuple4
import org.wysko.kmidi.midi.StandardMidiFile
import org.wysko.kmidi.midi.TimeBasedSequence.Companion.toTimeBasedSequence
import org.wysko.kmidi.midi.event.*
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.config.ConfigItemToolkit
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog

import kotlin.collections.*

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
            ExceptionDialog(IllegalArgumentException(),false,"构造器传入的 $kStdMidiFile 实例没有音符。")

        initSeatsMusic()//初始化各席位的乐谱
    }


    companion object {
        const val PERCUSSION_CHANNEL: Byte = 9//打击乐器的通道
        const val MSB_CONTROLLER: Byte = 0 //MSB控制器
        const val LSB_CONTROLLER: Byte = 32//LSB控制器

        /**
         * 从[ConfigItem]读取的默认乐器的值
         *
         * Triple三项分别为MSB，LSB，Program Change
         */
          val defaultInstrument: Triple<Byte, Byte, Byte> by lazy {
            try {
                ConfigItem.DefaultInstrument.load()
                    .split('_', limit = 3)
                    .map { it.toByte().takeIf { number -> number>=0 }?:throw NumberFormatException("范围不对") }
                    .let { Triple(it[0], it[1], it[2]) }
            }catch (e: Throwable){
                ExceptionDialog(e,true,"""
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties的设置项: DefaultInstrument，文本格式，或者范围不对：
        当前设置项的值为：${ConfigItem.DefaultInstrument.load()}

    正确格式为 <MSB>_<LSB>_<Program Change> ，值均为自然数，最大值为127。
    DefaultPercussion 用来代替 MSB=128 的情况

其他错误为未知错误。
        """.trimIndent())
//Acoustic Grand Piano
                Triple(0.toByte(),0.toByte(),0.toByte())
            }
        }

        /**
         * 从[ConfigItem]读取的默认打击乐器的值
         *
         * Pair两项分别为，LSB，Program Change。
         */
          val defaultPercussion: Pair<Byte, Byte> by lazy {
            try {
                ConfigItem.DefaultPercussion.load()
                    .split('_', limit = 2)
                    .map { it.toByte().takeIf { number -> number>=0 }?:throw NumberFormatException("范围不对") }
                    .let { Pair(it[0], it[1]) }
            }catch (e: Throwable){
                ExceptionDialog(e,true,"""
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties的设置项: DefaultPercussion，文本格式，或者范围不对：
        当前设置项的值为：${ConfigItem.DefaultPercussion.load()}
    正确格式为 <LSB>_<Program Change> ，值均为自然数，最大值为127。

其他错误为未知错误。

        """.trimIndent())
//Standard Kit
                Pair(0.toByte(),0.toByte() )
            }
        }



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
    private val timeBasedSequence = kStdMidiFile.toTimeBasedSequence().apply {
        channelSortedMidiEventsTracks.forEach { registerEvents(it.events) }//需要注册所有事件，以便TimeBasedSequence的API正常工作
    }


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
    private fun initSeatsMusic() {
        channelSortedMidiEventsTracks.forEach { track ->
            val firstEvent = track.events.first() as MidiEvent
            when (firstEvent.channel) {
                PERCUSSION_CHANNEL -> initPercussionMusic(track)
                else -> initNormalInstrumentMusic(track, firstEvent.channel)
            }
        }
    }

    /**
     * Process percussion track data.
     */
    private fun initPercussionMusic(track: StandardMidiFile.Track) {
        // Create fake MSB for percussion (always fixed)
        val fakeMSB = ControlChangeEvent(0, 0,0,0)
        val defaultLSB = ControlChangeEvent(0, PERCUSSION_CHANNEL, LSB_CONTROLLER, defaultPercussion.first)
        val defaultProgramChange = ProgramEvent(0, PERCUSSION_CHANNEL, defaultPercussion.second)

        // Extract instrument changes
        val instrumentChanges: List<PercussionInstrumentChange> = extractInstrumentChanges(
            events = ArrayDeque(track.events),
            msb = fakeMSB,
            lsb = defaultLSB,
            programChange = defaultProgramChange
        ).map { (_, lsb, programChange, tick) ->
            PercussionInstrumentChange(lsb, programChange, tick)
        }

        // Create percussion music entry
        percussionMusic.add(
            SeatPerformMusic.Percussion(
                timeBasedSequence.convertArcsToTimedArcs(track.arcs),
                instrumentChanges
            )
        )
    }

    /**
     * Process normal instrument track data.
     */
    private fun initNormalInstrumentMusic(track: StandardMidiFile.Track, channel: Byte) {
        val defaultMSB = ControlChangeEvent(0, channel, MSB_CONTROLLER, defaultInstrument.first)
        val defaultLSB = ControlChangeEvent(0, channel, LSB_CONTROLLER, defaultInstrument.second)
        val defaultProgramChange = ProgramEvent(0, channel, defaultInstrument.third)

        // Extract instrument changes
        val instrumentChanges: List<GeneralInstrumentChange> = extractInstrumentChanges(
            events = ArrayDeque(track.events),
            msb = defaultMSB,
            lsb = defaultLSB,
            programChange = defaultProgramChange
        ).map { (msb, lsb, programChange, tick) ->
            GeneralInstrumentChange(msb, lsb, programChange, tick)
        }

        // Create normal instrument music entry
        generalInstrumentMusic.add(
            SeatPerformMusic.GeneralInstrument(
                timeBasedSequence.convertArcsToTimedArcs(track.arcs),
                instrumentChanges
            )
        )
    }


    /*
    公共访问权限的API
     */

    /**
     * MIDI序列的总时长(非Tick长度)
     */
    val totalDuration = timeBasedSequence.duration


    /**
     * 指定事件发生的时间
     */
    fun getTimeOf(event: Event) = timeBasedSequence.getTimeOf(event)


    /**
     * Returns the duration of the sequence up to the specified tick.
     */
    fun getTimeAtTick(tick: Int) = timeBasedSequence.getTimeAtTick(tick)

    /**
     * 轨道
     *
     * 可能是个空轨道
     */
    val generalInstrumentMusic = ArrayDeque<SeatPerformMusic.GeneralInstrument>()
    val percussionMusic = ArrayDeque<SeatPerformMusic.Percussion>()


}
