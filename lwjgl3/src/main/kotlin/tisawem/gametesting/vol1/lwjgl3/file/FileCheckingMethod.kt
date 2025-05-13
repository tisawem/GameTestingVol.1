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

package tisawem.gametesting.vol1.lwjgl3.file

import arrow.core.Either
import arrow.core.Either.*
import org.jjazz.fluidsynthjava.api.FluidSynthJava
import org.wysko.kmidi.midi.reader.StandardMidiFileReader
import org.wysko.kmidi.midi.reader.readFile
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages.getMessages
import java.io.File
import javax.imageio.ImageIO
import javax.sound.midi.MidiSystem

/**
 * 提供一个函数，如果文件有效，返回Right(File)，反之，Left(String)，包含原因。
 *
 * 流程就是：路径是不是空白，路径是不是指向文件，能否解析这个文件，这个文件是否包含必要信息
 * @see  tisawem.gametesting.vol1.lwjgl3.swing.FileLoader.loadingFileFromJFileChooser
 * @param method 一个函数，接收一个[File]，返回[arrow.core.Either.Right]（File）或 [arrow.core.Either.Left]（String）
 */
enum class FileCheckingMethod(val method: (File) -> Either<String, File>) {
    MIDIFile( {
     try {
         MidiSystem.getSequence(it)//是不是javax.sound.midi承认的序列


         //是不是kmidi承认的MIDI文件，若是，检查是否有Arc事件。

         if (StandardMidiFileReader().readFile(it).tracks.any { track -> track.arcs.isNotEmpty() }) null
         else getMessages("MIDI_File_Without_Note")//有，返回文件，否则提示该文件无音符。

     }catch (_: Throwable){
         getMessages("Cannot_Load_This_File")
     }
    },true),
    SoundFont(issue = {

        val instance = FluidSynthJava()//创建FluidSynthJava实例
        try {
            instance.open(false)
            instance.loadSoundFont(it)//尝试读取
            null
        } catch (_: Throwable){
            getMessages("Cannot_Load_This_File")
        }finally {
//关闭实例
            instance.close()
        }
    }),
    Image( {it: File ->
        try {
            ImageIO.read(it)!!
            null
        }catch (_: Throwable){
            getMessages("Cannot_Load_This_File")
        }
    }as (File) -> String?);


    /**
     * ！！！！！！！！！！特别注意事项！！！！！！！！！！！
     *
     * notForPrimaryConstructor，它是占位用的，以防构造器冲突
     *
     * Overload resolution ambiguity between candidates:
     * constructor(method: (File) -> Either<String, File>): FileCheckingMethod
     * constructor(issue: (File) -> String?): FileCheckingMethod
     *
     * 注意枚举常量的写法：[MIDIFile]，[SoundFont]，[Image]
     * 一个传入了notForPrimaryConstructor形参，一个使用 issue = 指定x形参，一个使用强制转换 as (File) -> String?
     * 才会让编译器不报错
     *
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！
     */
    constructor( issue: (File) -> String?, notForPrimaryConstructor:Boolean=true) : this(method = {
        if (it.path.isBlank()) {
            Left(getMessages("File_Path_Is_Blank"))
        }
        if (!it.isFile) {
            Left(getMessages("Path_Not_Point_To_File"))
        }


        issue(it)?.let { issue ->
            Left(issue)
        }?: Right(it)


    })
}
