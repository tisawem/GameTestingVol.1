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

package tisawem.gametesting.vol1.file

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager



/**
 * 读取文件的类
 *
 * Either<String,File>，成功打开。返回Right(File)，否则Left(String)，包含理由。
 *
 */
object FileLoader {

    /**
     * 提供JFileChooser可用的FileFilter的枚举类
     *
     *
     * @constructor messages: 从messages.properties获取的字符串
     *
     * extensions: 文件拓展名
     *
     * @see  FileLoader.loadingFileFromJFileChooser
     */
    enum class FileFilter(val filter: javax.swing.filechooser.FileFilter) {
        MIDIFile("MIDI_File","mid"),
        SoundFont("SoundFont_File","sf2"),
        ;
        constructor(messages:String,vararg extensions:String  ):this(javax.swing.filechooser.FileNameExtensionFilter(getMessages(messages),*extensions))
    }




    /**
     * 通过JFileChooser打开文件，打开成功，返回文件，打开失败，返回字符串，包含原因。
     *
     *
     * @param fileFilter 供JFileChooser使用，可为null（不过滤），也可以从[FileFilter]获取。
     * @param fileCheckingMethod 提供一个函数，用于验证JFileChooser选择的文件，默认允许所有文件。
     *
     * 可以从[ FileCheckingMethod]获取相应验证方法。
     *
     * @see  FileFilter
     * @see  FileCheckingMethod
     * @return 成功打开时，返回Right(File)，否则返回Left(String)
     */
    fun loadingFileFromJFileChooser(
        fileFilter: javax.swing.filechooser.FileFilter?,
        fileCheckingMethod: (File) -> Either<String, File> = { Right(it) }
    ): Either<String, File> {
//设置外观

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())


        val fileChooser = JFileChooser(getMessages("Please_Select_A_MIDI_File")).apply {
            setFileSelectionMode(JFileChooser.FILES_ONLY)
            setFileFilter(fileFilter)
        }

        return when (fileChooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> fileCheckingMethod(fileChooser.selectedFile)
            JFileChooser.CANCEL_OPTION -> Left(getMessages("Canceled_Operation"))
            else -> Left("I_don't_know_what_happened_with_JFileChooser_window")

        }


    }


}



