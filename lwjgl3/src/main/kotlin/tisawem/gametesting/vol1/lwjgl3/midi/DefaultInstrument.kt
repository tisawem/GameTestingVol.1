package tisawem.gametesting.vol1.lwjgl3.midi

import org.wysko.kmidi.midi.event.ControlChangeEvent
import org.wysko.kmidi.midi.event.ProgramEvent
import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog

class DefaultInstrument(  val tick: Int,   val  channel: Byte) {
 companion object{
        const val MSB_CONTROLLER: Byte = 0 //MSB控制器
     const val LSB_CONTROLLER: Byte = 32//LSB控制器

     val percussionChannel: Byte = CoreConfig.PercussionChannel.load().toByteOrNull()?.takeIf { it>=0 && it<=15 }?:9//打击乐器的通道

     /**
      * 默认打击乐器
      *
      * 三项值为<MSB>_<LSB>_<Program Change>
      *
      * 第一项的MSB值，代表用什么值去替代 MSB=128
      *
      * 第一项MSB的值，范围是 -128 <= x <= -1
      *
      * 其余项的值，范围是 0 <= x <= 127
      *
      * x为整数
      */
     val defaultPercussion: Triple<Byte, Byte, Byte> by lazy {
         try {
             CoreConfig.DefaultPercussion.load()
                 .split('_', limit = 3)
                 .map { it.toByte() }
                 .let { Triple(it[0].takeIf { value -> value<0 }?:throw IndexOutOfBoundsException("MSB值范围不对"), it[1], it[2]) }
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
             Triple(0.toByte(),0.toByte(),0.toByte())
         }
     }

     /**
      * 默认非打击乐器
      *
      * 三项值为<MSB>_<LSB>_<Program Change> ，值均为自然数，最大值为127。
      */
     val defaultInstrument: Triple<Byte,Byte, Byte> by lazy {
         try {
             CoreConfig.DefaultInstrument.load()
                 .split('_', limit = 3)
                 .map { it.toByte().takeIf {value-> value>=0 }?:throw IndexOutOfBoundsException() }
                 .let { Triple(it[0], it[1],it[2]) }
         }catch (e: Throwable){
             ExceptionDialog(e,true,"""
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties的设置项: DefaultPercussion，文本格式，或者范围不对：
        当前设置项的值为：${CoreConfig.DefaultInstrument.load()}
    正确格式为 <MSB>_<LSB>_<Program Change> ，值均为自然数，最大值为127。

其他错误为未知错误。

        """.trimIndent())
//Standard Kit
             Triple((-1).toByte(),0.toByte(),0.toByte() )
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
     * controller和value取决于[DefaultInstrument.Companion]，value同时取决于[channel]是否等于[percussionChannel]
     */
    val defaultMSBEvent = getMSBControlChangeEvent(  (if (channel!=percussionChannel) defaultInstrument else defaultPercussion).first)

    /**
     * 获取默认的LSB CC事件
     *
     * 其事件的tick和channel值，取决于该实例的主构造函数传入了什么
     *
     * controller和value取决于[DefaultInstrument.Companion]，value同时取决于[channel]是否等于[percussionChannel]
     */

    val defaultLSBEvent = getLSBControlChangeEvent(  (if (channel!=percussionChannel) defaultInstrument else defaultPercussion).second)

    /**
     * 获取默认的[ProgramEvent]事件
     *
     * 其事件的tick和channel值，取决于该实例的主构造函数传入了什么
     *
     * value 取决于[DefaultInstrument.Companion]，以及[channel]是否等于[percussionChannel]
     */
    val defaultProgramEvent = getProgramEvent( (if (channel!=percussionChannel) defaultInstrument else defaultPercussion).third)



}
