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

package tisawem.gametesting.vol1.lwjgl3.midi

import org.wysko.kmidi.midi.event.ControlChangeEvent
import org.wysko.kmidi.midi.event.ProgramEvent
import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import tisawem.gametesting.vol1.midi.Instrument
import tisawem.gametesting.vol1.midi.InstrumentChange

class InstrumentStandard(val tick: Int, val  channel: Byte) {
    init {
        require(tick>=0&&channel>=0&&channel<=15){"tick需要大于0，channel值范围是0<=x<=15"}
    }

    /**
     * 如果想直接获得默认打击乐器，不考虑乐器变更事件发生在何时
     */
    constructor():this(0,  PERCUSSION_CHANNEL)

    /**
     * 如果只是想获得默认乐器，不考虑乐器变更事件发生在何时
     */
    constructor(channel: Byte):this(0,channel)


    companion object{
        const val MSB_CONTROLLER: Byte = 0 //MSB控制器
     const val LSB_CONTROLLER: Byte = 32//LSB控制器

     val PERCUSSION_CHANNEL: Byte = CoreConfig.PercussionChannel.load().toByteOrNull()?.takeIf { it>=0 && it<=15 }?:9//打击乐器的通道

     /**
      * 默认打击乐器
      *
      *
      * 第一项的MSB值，代表用什么值去替代 MSB=128
      *
      * 第一项MSB的值，范围是 -128 <= x <= -1
      *
      * 其余项的值，范围是 0 <= x <= 127
      *
      * x为整数
      */
     val defaultPercussion by lazy {
         try {
             CoreConfig.DefaultPercussion.load()
                 .split('_', limit = 3)
                 .map { it.toByte() }
                 .let { Instrument(it[0].takeIf { value -> value<0 }?:throw IndexOutOfBoundsException("MSB值必须是负数"), it[1].takeIf { value -> value>=0 }?:throw IndexOutOfBoundsException("LSB值不得为负数"), it[2].takeIf { value -> value>=0 }?:throw IndexOutOfBoundsException("Program Change值不得为负数")) }
         }catch (e: Throwable){
             ExceptionDialog(e,true,"""
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties的设置项: DefaultInstrument，文本格式，或者范围不对：
        当前设置项的值为：${CoreConfig.DefaultInstrument.load()}

    正确格式为 <MSB>_<LSB>_<Program Change>
    DefaultPercussion 用来代替 MSB=128 的情况
    所以，第一项的MSB值，代表用什么值去替代 MSB=128

    第一项MSB的值，范围是 -128 <= x <= -1
    其余项的值，范围是 0 <= x <= 127
    x为整数

其他错误为未知错误。
        """.trimIndent())
//Acoustic Grand Piano
             Instrument(0.toByte(),0.toByte(),0.toByte())
         }
     }

     /**
      * 默认非打击乐器
      *
      * 三项值均为自然数，最大值为127。
      */
     val defaultInstrument by lazy {
         try {
             CoreConfig.DefaultInstrument.load()
                 .split('_', limit = 3)
                 .map { it.toByte().takeIf {value-> value>=0 }?:throw IndexOutOfBoundsException() }
                 .let { Instrument(it[0], it[1],it[2]) }
         }catch (e: Throwable){
             ExceptionDialog(e,true,"""
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties的设置项: DefaultPercussion，文本格式，或者范围不对：
        当前设置项的值为：${CoreConfig.DefaultInstrument.load()}
    正确格式为 <MSB>_<LSB>_<Program Change> ，值均为自然数，最大值为127。

其他错误为未知错误。

        """.trimIndent())
//Standard Kit
             Instrument((-1).toByte(),0.toByte(),0.toByte() )
         }
     }

 }


    fun getMSBControlChangeEvent (      value:Byte)= ControlChangeEvent(tick,channel,MSB_CONTROLLER,value)
    fun getLSBControlChangeEvent (       value:Byte)= ControlChangeEvent(tick,channel,LSB_CONTROLLER,value)
    fun getProgramEvent(program: Byte)= ProgramEvent(tick,channel,program)

    /**
     * 获取默认的MSB CC事件
    * 其事件的tick和channel值，取决于该实例的主构造函数传入了什么
     *
     * controller和value取决于[InstrumentStandard.Companion]，value同时取决于[channel]是否等于[PERCUSSION_CHANNEL]
     */
    val defaultMSBEvent = getMSBControlChangeEvent(  (if (channel!=PERCUSSION_CHANNEL) defaultInstrument else defaultPercussion).msb)

    /**
     * 获取默认的LSB CC事件
     *
     * 其事件的tick和channel值，取决于该实例的主构造函数传入了什么
     *
     * controller和value取决于[InstrumentStandard.Companion]，value同时取决于[channel]是否等于[PERCUSSION_CHANNEL]
     */

    val defaultLSBEvent = getLSBControlChangeEvent(  (if (channel!=PERCUSSION_CHANNEL) defaultInstrument else defaultPercussion).lsb)

    /**
     * 获取默认的[ProgramEvent]事件
     *
     * 其事件的tick和channel值，取决于该实例的主构造函数传入了什么
     *
     * value 取决于[InstrumentStandard.Companion]，以及[channel]是否等于[PERCUSSION_CHANNEL]
     */
    val defaultProgramEvent = getProgramEvent( (if (channel!=PERCUSSION_CHANNEL) defaultInstrument else defaultPercussion).prg)

val defaultInstrumentChange= InstrumentChange(defaultMSBEvent,defaultLSBEvent,defaultProgramEvent,tick)

}
