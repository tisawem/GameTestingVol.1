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

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import tisawem.gametesting.vol1.Bridge
import tisawem.gametesting.vol1.gdx.Game
import tisawem.gametesting.vol1.gdx.screen.Perform
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages.getMessages
import tisawem.gametesting.vol1.lwjgl3.midi.MidiValidationService
import tisawem.gametesting.vol1.lwjgl3.midi.player.MidiDeviceManager
import tisawem.gametesting.vol1.lwjgl3.toolkit.Toolkit
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.io.File
import javax.swing.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Play(frame: JFrame) : JDialog(frame, true) {
    companion object {
        private fun JLabel.usingGlobalProperties(): JLabel {
            font = Font(null, Font.BOLD, 24)

            return this
        }

        private fun AbstractButton.usingGlobalProperties(): AbstractButton {
            font = Font(null, Font.BOLD, 24)
            return this
        }


        private fun JTextArea.usingGlobalProperties(): JTextArea {
            isEditable = false
            font = Font(null, Font.PLAIN, 24)
            lineWrap = true
            wrapStyleWord = true
            return this
        }

        /**
         * 将字符串列表，转为按行排列的字符串
         */
        private tailrec fun List<String>.stringListToString(index: Int = 0, string: String = ""): String =
            if (index >= size) {
                string
            } else {
                stringListToString(
                    index + 1,
                    "$string${if (string.isEmpty()) "" else "\n\n"/*以免第一行空行*/}${get(index)}"
                )
            }

    }

    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    private val problems = MidiValidationService.validate()

    /*
    --------------North
     */
    private val problemLabel = JLabel(getMessages("Following_Problems_Need_Solved")).usingGlobalProperties()

    /*
    ----------- --Center
     */
    private val problemTextArea = JTextArea(problems.stringListToString()).usingGlobalProperties()
    private val problemScrollPane = JScrollPane(problemTextArea)

    /*
    --------------South
     */
    private val backButton = JButton(getMessages("Back")).usingGlobalProperties().apply {
        addActionListener {
            this@Play.dispose()
        }
    }


    private val mainPanel = JPanel(BorderLayout(10, 10)).apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 添加边缘边距
        add(problemLabel, BorderLayout.NORTH)
        add(problemScrollPane, BorderLayout.CENTER)
        add(backButton, BorderLayout.SOUTH)
    }


    init {
        if (problems.isEmpty()) {

            try {
                dispose()
                frame.isVisible = false

                val processedMIDIData = MidiValidationService.createMidiEventProcess()!!
                val player = MidiDeviceManager.getPlayer(File(DesktopConfig.MIDIFile.load()))

                val bridge = object : Bridge {
                    override val timedBaseSequence = processedMIDIData.timeBasedSequence

                    override fun create(readyCallBack: (() -> Unit)?, finishCallBack: (() -> Unit)?) {
                        player.readyCallback = readyCallBack
                        player.finishCallback = finishCallBack
                    }

                    override fun play() {
                        player.play()
                    }


                    override fun stop() {
                        player.stop()

                    }

                    override fun getPosition(): Duration? =
                        player.getMicroSecondPosition()?.toDuration(DurationUnit.MICROSECONDS)

                    override val score = processedMIDIData.scores

                }









                Lwjgl3Application(Game(bridge) {
                    it.addScreen<Perform>(Perform(it))
                    it.setScreen<Perform>()
                }, Lwjgl3ApplicationConfiguration().apply {
                    title = "GameTestingVol.1  ${DesktopConfig.MIDIFile.load()}"
                    if (DesktopConfig.FullScreen.load().toBoolean()) {
                        setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())
                    } else {
                        val (w, h) = Toolkit.getWindowedResolution()
                        setWindowedMode(w, h)
                    }

                })
            } catch (e: Throwable) {
                ExceptionDialog(e, true, "未知错误\n可以尝试再播放一遍。")
            } finally {

                frame.isVisible = true
            }


        } else {

            add(mainPanel)
            pack()
            minimumSize = Dimension(size.width, size.height * 2)
            isVisible = true
        }
    }
}
