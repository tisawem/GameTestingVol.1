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

package tisawem.gametesting.vol1.lwjgl3.swing

import arrow.core.Either
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages.getMessages
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.UIManager
import javax.swing.filechooser.FileFilter


/**
 * 读取文件的类
 *
 * Either<String,File>，成功打开。返回Right(File)，否则Left(String)，包含理由。
 *
 */
object FileLoader {


    /**
     * 通过JFileChooser打开文件，打开成功，返回文件，打开失败，返回字符串，包含原因。
     *
     *
     * @param filter 供JFileChooser使用，可为null（不过滤），也可以从[FileFilter]获取。
     * @param fileCheckingMethod 提供一个函数，用于验证JFileChooser选择的文件，默认允许所有文件。
     *
     * 可以从[ FileCheckingMethod]获取相应验证方法。
     *
     * @see  FileFilter
     * @see  tisawem.gametesting.vol1.lwjgl3.file.FileCheckingMethod
     * @return 成功打开时，返回Right(File)，否则返回Left(String)
     */
    fun loadingFileFromJFileChooser(
        currentPath: String="",
        filter: FileFilter?,
        fileCheckingMethod: (File) -> Either<String, File> = { Either.Right(it) }
    ): Either<String, File> {
//设置外观

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())


        val fileChooser = JFileChooser(currentPath).apply {
            setFileSelectionMode(JFileChooser.FILES_ONLY)
            setFileFilter(filter)
        }

        return when (fileChooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> fileCheckingMethod(fileChooser.selectedFile)
            JFileChooser.CANCEL_OPTION -> Either.Left(getMessages("Canceled_Operation"))
            else -> Either.Left("JFileChooser_Window_Error")

        }


    }

    tailrec fun loopingAskUserForFileOrAbandon(fileObtainMethod: ()->Either<String, File>  ):File? =when (val file=fileObtainMethod()) {
        is Either.Left<*> -> {
            val result = JOptionPane.showConfirmDialog(
                null, // 父组件
                "${file.leftOrNull()}\n${getMessages("Open_Again")}",
                "", // 对话框标题
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )

            when (result) {
                JOptionPane.YES_OPTION ->loopingAskUserForFileOrAbandon(fileObtainMethod)
                else -> null
            }
        }

        is Either.Right<*> -> {
            file.getOrNull()
        }
    }

}
