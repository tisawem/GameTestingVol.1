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

package tisawem.gametesting.vol1.ui.swing

import com.badlogic.gdx.Gdx
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.config.Config
import tisawem.gametesting.vol1.file.ExtensionFilter
import tisawem.gametesting.vol1.file.FileCheckingMethod
import tisawem.gametesting.vol1.i18n.Messages
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.toolkit.Toolkit
import tisawem.gametesting.vol1.ui.gdx.screen.HomePageIDLE
import tisawem.gametesting.vol1.ui.swing.FileLoader.loopingAskUserForFileOrAbandon
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Settings(frame: JFrame, val game: KtxGame<KtxScreen>) : JDialog(frame, getMessages("Settings"), true) {

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
                    Config.PerformBackgroundImage.load(),
                    ExtensionFilter.Image.filter(),
                    FileCheckingMethod.Image.method
                )
            }?.let {
                Config.PerformBackgroundImage.write(it.canonicalPath)
                game.getScreen<HomePageIDLE>().backgroundChanged=true


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


        val using = Config.UsingGervill.load().toBoolean()
        val fluid = JRadioButton("FluidSynth", !using).apply {
            usingGlobalProperties()
            toolTipText = getMessages("FluidSynth_Tips")
            addChangeListener {
                Config.UsingGervill.write("false")
            }
        }
        val gervill = JRadioButton("Gervill", using).apply {
            usingGlobalProperties()
            toolTipText = getMessages("Gervill_Tips")
            addChangeListener {
                Config.UsingGervill.write("false")
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
        isSelected = Config.FullScreen.load().toBoolean()
        addActionListener {
            Config.FullScreen.write(isSelected.toString())
        }
    }

    private val languageLabel = JLabel(getMessages("Change_Language")).usingGlobalProperties()
    private val languageBox = JComboBox(Messages.SupportedLanguage.entries.toTypedArray()).apply {
        selectedItem= null
        font = Font(null, Font.PLAIN, 28)
        addActionListener {
            selectedItem?.let {
                Config.Language.write((it as Messages.SupportedLanguage).locale.name)
                dispose()
                //无需在此处关闭HomePage的实例，在关闭HomePageIDLE时，会关闭HomePage的实例
                game.getScreen<HomePageIDLE>() .languageChanged = true


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
        (Config.ScreenAdvancedTime.load().toDoubleOrNull() ?: 0.0).toDuration(DurationUnit.SECONDS)
            .toInt(DurationUnit.MILLISECONDS)
    ).apply {
        advanceTImeLabel.text = "${getMessages("Screen_Advance_Time")} $value"

        font = Font(null, Font.PLAIN, 18)
        toolTipText = getMessages("Screen_Advance_Time_Tips")
        majorTickSpacing = 500
        minorTickSpacing = 100
        paintTicks = true
        paintLabels = true

        addChangeListener {
            Config.ScreenAdvancedTime.write(
                value.toDuration(DurationUnit.MILLISECONDS).toDouble(DurationUnit.SECONDS).toString()
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
                    Gdx.files.internal(Config.PerformBackgroundImage.load())
                } catch (_: Throwable) {
                    Gdx.files.external(Config.PerformBackgroundImage.load())
                }.read()
            )
            //与 Will_Used_Background_Image 文本栏同宽
            val scale = currentImageLabel.width / image.width.toFloat()
            currentImage.changeLabelIcon(image, scale).text=null



        } catch (_: Throwable) {
          //空着，不显示
        }

//调整最小的窗口大小，高度480完美，宽度根据调整后的窗口规定。
        // 这样，用户调节窗口时，就一定不会把元素堆叠
        minimumSize= Dimension(width,480)

        setLocationRelativeTo(frame) // 窗口居中
        isVisible = true
    }
}
