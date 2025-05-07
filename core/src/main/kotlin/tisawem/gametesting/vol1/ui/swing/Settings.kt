package tisawem.gametesting.vol1.ui.swing

import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.config.Config
import tisawem.gametesting.vol1.i18n.Messages
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.ui.gdx.screen.HomePageIDLE
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import javax.swing.AbstractButton
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JSlider
import javax.swing.UIManager
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Settings(frame: JFrame,val game: KtxGame<KtxScreen>): JDialog(frame,getMessages("Settings"),true) {

    companion object{
        private fun JLabel.usingGlobalProperties(): JLabel {
            font = Font(null, Font.BOLD, 24)

            return this
        }

        private fun AbstractButton.usingGlobalProperties(): AbstractButton {
            font = Font(null, Font.BOLD, 24)
            return this
        }

    }


    init {
        UIManager.setLookAndFeel(  UIManager.getSystemLookAndFeelClassName())
        defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    /*
    North
     */
    private val backButton= JButton(getMessages("Back")).apply {
     usingGlobalProperties()
        addActionListener {
            dispose()
        }

    }

    /*
    West
     */



    private val engineLabel=JLabel(getMessages("SoundFont_Engine")).usingGlobalProperties()


private val westGroup= JPanel(GridLayout(4,1)).apply{


    val using=Config.UsingGervill.load().toBoolean()
    val fluid=JRadioButton("FluidSynth",!using ).apply {
        usingGlobalProperties()
        toolTipText=getMessages("FluidSynth_Tips")
        addChangeListener {
            Config.UsingGervill.write("false")
        }
    }
    val gervill=JRadioButton("Gervill",using).apply {
        usingGlobalProperties()
        toolTipText=getMessages("Gervill_Tips")
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

    private val fullScreenCheckBox= JCheckBox(getMessages("FullScreen")).apply {
        font= Font(null, Font.BOLD,24)
        toolTipText=getMessages("FullScreen_Tips")
        isSelected= Config.FullScreen.load().toBoolean()
        addActionListener {
            Config.FullScreen.write(isSelected.toString())
        }
    }

    private val languageLabel=JLabel(getMessages("Change_Language")).usingGlobalProperties()
    private val languageBox= JComboBox(Messages.SupportedLanguage.entries. toTypedArray()).apply {
        font= Font(null,Font.PLAIN,28)
        addActionListener {
            selectedItem?.let {
                Config.Language.write((it as Messages.SupportedLanguage).locale.name)
                dispose()
                frame.dispose()
                HomePageIDLE.languageChanged=true



            }
        }
    }

private val eastPanel=JPanel(GridLayout(3,1)).apply {
    add(fullScreenCheckBox)
    add(languageLabel)
    add(languageBox)
}

    /*
     South
     */
    private val advanceTImeLabel= JLabel().usingGlobalProperties()
    private val advanceTimeSlider= JSlider(-1000,1000, (Config.ScreenAdvancedTime.load().toDoubleOrNull()?:0.0).toDuration(DurationUnit.SECONDS).toInt( DurationUnit.MILLISECONDS)).apply {
        advanceTImeLabel.text="${getMessages("Screen_Advance_Time")} $value"

        font=Font(null, Font.PLAIN,18)
        toolTipText=getMessages("Screen_Advance_Time_Tips")
        majorTickSpacing=500
        minorTickSpacing=100
        paintTicks=true
        paintLabels=true

        addChangeListener {
Config.ScreenAdvancedTime.write(value.toDuration(DurationUnit.MILLISECONDS).toDouble(DurationUnit.SECONDS).toString())
            advanceTImeLabel.text="${getMessages("Screen_Advance_Time")} $value"

        }

}

private val southPanel= JPanel(GridLayout(2,1)).apply {
    add(advanceTImeLabel)
    add(advanceTimeSlider)
}



    private val mainPanel= JPanel(BorderLayout(10,10)).apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 添加边缘边距
        add(backButton, BorderLayout.NORTH)
        add(westGroup, BorderLayout.WEST)
        add(eastPanel, BorderLayout.EAST)
        add(southPanel, BorderLayout.SOUTH)
    }



    init {
      add(mainPanel)


        pack() // 根据组件首选大小调整窗口
        minimumSize = Dimension(640, 480)
         setLocationRelativeTo(frame) // 窗口居中
        isVisible=true
    }
}
