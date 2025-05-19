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

package tisawem.gametesting.vol1.lwjgl3.swing

import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.file.ExtensionFilter
import tisawem.gametesting.vol1.lwjgl3.file.FileCheckingMethod
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages.getMessages
import tisawem.gametesting.vol1.lwjgl3.swing.FileLoader.loopingAskUserForFileOrAbandon
import tisawem.gametesting.vol1.lwjgl3.toolkit.Toolkit
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

class Settings(frame: JFrame ) : JDialog(frame, getMessages("Settings"), true) {

    companion object {
        private fun JLabel.usingGlobalProperties(): JLabel {
            font = Font(null, Font.BOLD, 24)

            return this
        }

        private fun AbstractButton.usingGlobalProperties(): AbstractButton {
            font = Font(null, Font.BOLD, 24)
            return this
        }

        private fun JLabel.changeLabelIcon(image: BufferedImage, scale: Float): JLabel {

            this.icon = ImageIcon(Toolkit.getScaledBufferedImage(image, scale))

            return this

        }

    }


    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    /*
    North
     */
    private val backButton = JButton(getMessages("Back")).apply {
        usingGlobalProperties()
        addActionListener {
            dispose()
        }

    }

    /*
     * West
     */

    private val openImageButton = JButton(getMessages("Open_Background_Image")).apply {
        usingGlobalProperties()

        addActionListener {
            loopingAskUserForFileOrAbandon {
                FileLoader.loadingFileFromJFileChooser(
                    CoreConfig.PerformBackgroundImage.load(),
                    ExtensionFilter.Image.filter(),
                    { FileCheckingMethod.Image.method(it) }
                )
            }?.let {
                CoreConfig.PerformBackgroundImage.write(it.canonicalPath)


                try {

                    val image = ImageIO.read(it)

                    //与 Will_Used_Background_Image 文本栏同宽
                    val scale = currentImageLabel.width / image.width.toFloat()

                    currentImage.changeLabelIcon(image, scale).text = null
                  this@Settings.minimumSize = Dimension(this@Settings.size.width,480)

                } catch (_: Throwable) { /* 空着，不显示 */}
            }

        }
    }

    private val currentImageLabel = JLabel(getMessages("Will_Used_Background_Image")).usingGlobalProperties()

    private val currentImage = JLabel().usingGlobalProperties()

    private val westGroup = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        toolTipText = getMessages("Background_Image_Tips")
        add(openImageButton)
        add(currentImageLabel)
        add(currentImage)
    }

    /*
    Center
     */


    private val engineLabel = JLabel(getMessages("SoundFont_Engine")).usingGlobalProperties()


    private val centerGroup = JPanel(GridLayout(4, 1)).apply {

        val using = DesktopConfig.UsingGervill.load().toBoolean()
        val fluid = JRadioButton("FluidSynth", !using).apply {
            usingGlobalProperties()
            toolTipText = getMessages("FluidSynth_Tips")
            addActionListener {
                if (isSelected) {
                    DesktopConfig.UsingGervill.write("false")
                }
            }
        }
        val gervill = JRadioButton("Gervill", using).apply {
            usingGlobalProperties()
            toolTipText = getMessages("Gervill_Tips")
            addActionListener {
                if (isSelected) {
                    DesktopConfig.UsingGervill.write("true")
                }
            }
        }
        ButtonGroup().apply {
            add(fluid)
            add(gervill)
        }

        add(engineLabel)
        add(fluid)
        add(gervill)
    }

    /*
East
 */

    private val fullScreenCheckBox = JCheckBox(getMessages("FullScreen")).apply {
        font = Font(null, Font.BOLD, 24)
        toolTipText = getMessages("FullScreen_Tips")
        isSelected = DesktopConfig.FullScreen.load().toBoolean()
        addActionListener {
            DesktopConfig.FullScreen.write(isSelected.toString())
        }
    }

    private val languageLabel = JLabel(getMessages("Change_Language")).usingGlobalProperties()
    private val languageBox = JComboBox(Messages.SupportedLanguage.entries.toTypedArray()).apply {
        selectedItem= null
        font = Font(null, Font.PLAIN, 28)
        addActionListener {
            selectedItem?.let {
                CoreConfig.Language.write((it as Messages.SupportedLanguage).locale.name)
                dispose()
                frame.dispose()
                HomePage()


            }
        }
    }

    private val eastPanel = JPanel(GridLayout(3, 1)).apply {
        add(fullScreenCheckBox)
        add(languageLabel)
        add(languageBox)
    }

    /*
     South
     */
    private val advanceTImeLabel = JLabel().usingGlobalProperties()
    private val advanceTimeSlider = JSlider(
        -1000,
        1000,
        (CoreConfig.ScreenAdvancedTime.load().toIntOrNull() ?: 0)
    ).apply {
        advanceTImeLabel.text = "${getMessages("Screen_Advance_Time")} $value"

        font = Font(null, Font.PLAIN, 18)
        toolTipText = getMessages("Screen_Advance_Time_Tips")
        majorTickSpacing = 500
        minorTickSpacing = 100
        paintTicks = true
        paintLabels = true

        addChangeListener {
            CoreConfig.ScreenAdvancedTime.write(
                value.toString()
            )
            advanceTImeLabel.text = "${getMessages("Screen_Advance_Time")} $value"

        }

    }

    private val southPanel = JPanel(GridLayout(2, 1)).apply {
        add(advanceTImeLabel)
        add(advanceTimeSlider)
    }


    private val mainPanel = JPanel(BorderLayout(10, 10)).apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 添加边缘边距
        add(westGroup, BorderLayout.WEST)
        add(backButton, BorderLayout.NORTH)
        add(centerGroup, BorderLayout.CENTER)
        add(eastPanel, BorderLayout.EAST)
        add(southPanel, BorderLayout.SOUTH)
    }


    init {
        add(mainPanel)


        pack() // 根据组件首选大小调整窗口


        //在执行 pack() 前，currentImageLabel.width 是 0，会导致缩放错误
        try {
            val image = ImageIO.read(
                try {
                    Settings::class.java.classLoader.getResourceAsStream(CoreConfig.PerformBackgroundImage.load())!!
                } catch (_: Throwable) {
                    File(CoreConfig.PerformBackgroundImage.load()).inputStream()
                }
            )
            //与 Will_Used_Background_Image 文本栏同宽
            val scale = currentImageLabel.width / image.width.toFloat()
            currentImage.changeLabelIcon(image, scale).text=null



        } catch (_: Throwable) {
          //空着，不显示
        }

//调整最小的窗口大小，高度480完美，宽度根据调整后的窗口规定。
        // 这样，用户调节窗口时，就一定不会把元素堆叠
      pack()
        minimumSize=size

        setLocationRelativeTo(frame) // 窗口居中
        isVisible = true
    }
}
