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

package tisawem.gametesting.vol1.ui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import kotlin.system.exitProcess

/**
 * A dialog window designed to display exception details and provide user interaction options.
 *
 * This dialog presents information about a throwable error, including its stack trace and an optional description.
 * It allows the user to decide whether to continue using the program (if possible) or exit. The dialog is modal,
 * always on top, and uses the system's look and feel for a native appearance.
 *
 * The dialog includes:
 * - A message label indicating whether the program can continue running or must exit.
 * - A scrollable text area displaying the error description and stack trace.
 * - Buttons for continuing the program (if allowed) and exiting the program.
 *
 * If the program cannot continue, the dialog ensures the application exits when closed. The exit behavior can be
 * customized by providing an `onExit` callback function.
 *
 * The dialog prints the stack trace of the throwable to the console upon initialization for debugging purposes.
 *
 *
 * @param throwable 想要抛出的错误
 * @param canContinue 程序能否继续运行，或者强制抛出错误，结束程序。
 *true选项，通常用于替代[printStackTrace]函数。
 * @param description 给用户看的错误描述
 * @param onExit 强制退出的执行脚本，也就是点击退出按钮要执行的脚本。
 */
class ExceptionDialog(
    throwable: Throwable,  canContinue: Boolean, description: String? = null,onExit: () -> Unit = {
        com.badlogic.gdx.Gdx.app?.exit()
        exitProcess(-1)
    }
) {

    private val dialog = JDialog().apply {

        // 设置对话框属性
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isModal = true
        isAlwaysOnTop = true
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    }


    // 错误信息 - 根据canContinue状态定制
    private val messageLabel = JLabel(
        """
                <html>
                程序运行异常，${if (canContinue) "但可以继续运行" else "只能退出程序"}。请选择：<br>
                The program encountered an error, ${if (canContinue) "but it can continue running" else "and needs to exit"}. Please select:
                </html>
                """.trimIndent()
    ).apply {
        font = Font(null, Font.BOLD, 16)
    }

    // 创建一个面板来放置按钮
    private val buttonPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        border = BorderFactory.createEmptyBorder(5, 0, 0, 0)  // 添加顶部边距

        if (canContinue) {
            // 如果程序可以继续，添加继续按钮
            val continueButton = JButton(
                """
                        <html>
                            继续使用程序<br>
                            Continue Using Program
                        </html>
                        """.trimIndent()
            ).apply {
                font = Font(null, Font.PLAIN, 16)
                addActionListener { dialog.dispose() }
            }
            add(continueButton)

            // 添加一些按钮之间的间距
            add(Box.createRigidArea(Dimension(10, 0)))


        }
        //退出按钮
        val exitButton = JButton(
            """
                        <html>
                            退出程序<br>
                            Exit Program
                        </html>
                        """.trimIndent()
        ).apply {
            font = Font(null, Font.PLAIN, 16)
            addActionListener { onExit() }
        }
        add(exitButton)
    }


    // 带滚动条的栈帧信息文本区域
    private val errorDescriptionArea = JTextArea(
        "错误描述 / Error Description:${if (description == null) "" else "\n\n$description"}\n\n${throwable.stackTraceToString()}"
    ).apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        font = Font(null, Font.PLAIN, 14)
    }

    val scrollPane = JScrollPane(errorDescriptionArea).apply {
        preferredSize = Dimension(800, 300)
    }


    // 创建自定义的背景面板
    val panel = JPanel(BorderLayout(10, 10)).apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        // 添加组件到面板
        add(messageLabel, BorderLayout.NORTH)
        add(buttonPanel, BorderLayout.CENTER)
        add(scrollPane, BorderLayout.SOUTH)
    }

    init {
        // 在控制台打印栈帧信息
        throwable.printStackTrace()


        // 设置对话框的内容面板
        dialog.apply {
            contentPane = panel

            // 调整大小并居中显示
            pack()
            setLocationRelativeTo(null)

            // 如果程序不能继续，当对话框关闭时直接退出
            if (!canContinue) {
                addWindowListener(object : WindowAdapter() {
                    override fun windowClosed(e: WindowEvent) {
                        onExit()
                    }

                    override fun windowClosing(e: WindowEvent) {
                        onExit()
                    }
                })
            }

            // 显示对话框
            isVisible = true
        }


    }
}
