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


import com.badlogic.gdx.Gdx
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.*
import kotlin.system.exitProcess

class ExceptionDialog(
    throwable: Throwable,
    private val canContinue: Boolean,
    description: String = "",
    val onExit: () -> Nothing = {
        try {
            Gdx.app.exit()
        } catch (_: Throwable) {}
        exitProcess(-1)
    }
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionDialog::class.java)
    }

    private val dialog = JDialog().apply {
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        isModal = true
        isAlwaysOnTop = true
        minimumSize = Dimension(640, 480)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        if (!canContinue) {
            addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent) {
                    logger.info("Exception dialog closed by window event - exiting program")
                    onExit()
                }
                override fun windowClosing(e: WindowEvent) {
                    logger.info("Exception dialog closing - exiting program")
                    onExit()
                }
            })
        }

        // 强制焦点恢复
        addWindowFocusListener(object : WindowFocusListener {
            override fun windowGainedFocus(e: WindowEvent?) {
                logger.debug("Dialog gained focus")
            }
            override fun windowLostFocus(e: WindowEvent?) {
                logger.debug("Dialog lost focus, attempting to regain")
                SwingUtilities.invokeLater {
                    toFront()
                    requestFocus()
                }
            }
        })
    }

    private val messageLabel = JLabel(
        """
        <html>
        程序运行异常，${if (canContinue) "但可以继续运行" else "只能退出程序"}。请选择：<br>
        The program encountered an error, ${if (canContinue) "but it can continue running" else "and needs to exit"}. Please select:
        </html>
        """.trimIndent()
    ).apply { font = Font(null, Font.BOLD, 16) }

    private val buttonPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        border = BorderFactory.createEmptyBorder(5, 0, 0, 0)

        if (canContinue) {
            val continueButton = JButton(
                """
                <html>
                    继续使用程序<br>
                    Continue Using Program
                </html>
                """.trimIndent()
            ).apply {
                font = Font(null, Font.PLAIN, 16)
                addActionListener {
                    logger.info("User chose to continue using the program")
                    logger.debug("Closing exception dialog after user chose to continue")
                    dialog.dispose()
                }
            }
            add(continueButton)
            add(Box.createRigidArea(Dimension(10, 0)))
        }

        val exitButton = JButton(
            """
            <html>
                退出程序<br>
                Exit Program
            </html>
            """.trimIndent()
        ).apply {
            font = Font(null, Font.PLAIN, 16)
            addActionListener {
                logger.info("User chose to exit the program")
                onExit()
            }
        }
        add(exitButton)
    }

    private val errorDescriptionArea = JTextArea(
        "错误描述 / Error Description:\n\n$description\n\n${throwable.stackTraceToString()}"
    ).apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        font = Font(null, Font.PLAIN, 14)
    }

    private val scrollPane = JScrollPane(errorDescriptionArea)

    private val panel = JPanel(BorderLayout(10, 10)).apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        add(messageLabel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
    }

    init {
        // 记录异常信息
        logger.error("Creating exception dialog - canContinue: $canContinue", throwable)
        if (description.isNotBlank()) {
            logger.error("Error description: $description")
        }

        // 同时输出到标准错误流（保留原有行为）
        throwable.printStackTrace()

        dialog.apply {
            contentPane = panel
            pack()
            setLocationRelativeTo(null)

            try {
                logger.debug("Attempting to set windowed mode to ${size.width}x${size.height}")
                Gdx.graphics.setWindowedMode(size.width, size.height)
            } catch (e: Throwable) {
                logger.warn("Failed to set windowed mode", e)
            }

            logger.info("Showing exception dialog")
            isVisible = true

            SwingUtilities.invokeLater {
                toFront()
                requestFocus()
                logger.debug("Dialog brought to front and focused")
            }
        }
    }
}

