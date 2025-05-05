package tisawem.gametesting.vol1.ui.swing

import arrow.core.Either
import com.badlogic.gdx.Gdx
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.file.ExtensionFilter
import tisawem.gametesting.vol1.file.FileCheckingMethod
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.midi.synth.MidiDeviceManager
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*

class HomePage(val game: KtxGame<KtxScreen>) : JFrame("GameTestingVol.1 ${getMessages("HomePage")}") {
    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    }


    companion object {

        /*
        统一的组件外观
         */

        private fun JLabel.usingGlobalProperties(): JLabel {
            font = Font(null, Font.BOLD, 24)

            return this
        }

        private fun JButton.usingGlobalProperties(): JButton {
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


    /*
    ----------------------West
     */


    private val play = JButton(getMessages("Play")).apply {
        usingGlobalProperties()
        toolTipText = getMessages("Play_Tips")
    }

    private val settings = JButton(getMessages("Settings")).usingGlobalProperties()

    private val openMidiFile = JButton(getMessages("Open_MIDI_File")).apply {
        usingGlobalProperties()
        toolTipText = getMessages("Open_MIDI_File_Tips")
        addActionListener {
           loopingAskUserForFileOrAbandon({FileLoader.loadingFileFromJFileChooser(
               ExtensionFilter.MIDIFile.filter,
               FileCheckingMethod.MIDIFile.method
           )})?.let { ConfigItem.MIDIFile.write(it.canonicalPath)
           midiFilePathTextArea.text=it.canonicalPath

           }

        }
    }
    private val openSoundFont = JButton(getMessages("Open_SoundFont")).apply {
        usingGlobalProperties()
        toolTipText = getMessages("Open_SoundFont_Tips")
        addActionListener {
            loopingAskUserForFileOrAbandon({FileLoader.loadingFileFromJFileChooser(
                ExtensionFilter.SoundFont.filter,
                FileCheckingMethod.SoundFont.method
            )})?.let { ConfigItem.MIDIOutputDevice.write(it.canonicalPath)
                deviceOrSf2PathTextArea.text=it.canonicalPath

            }

        }
    }
    private val exit = JButton(getMessages("Exit")).apply {
        usingGlobalProperties()
        addActionListener {
            this@HomePage.dispose()
        }
    }
    private val buttons = JPanel(GridLayout(5, 1, 5, 5)).apply {
        add(play)
        add(settings)
        add(openMidiFile)
        add(openSoundFont)
        add(exit)
    }

    /*
  ----------------------South
   */
    private val midiDeviceLabel = JLabel(getMessages("Output_MIDI_Device")).usingGlobalProperties()
    private val midiDevice = JComboBox(MidiDeviceManager.getAvailableMIDIOutputDevices().toTypedArray()).apply {
        font = Font(null, Font.PLAIN, 28)
    }

    private val midiDevicePanel = JPanel(GridLayout(2, 1, 5, 5)).apply {
        add(midiDeviceLabel)
        add(midiDevice)
    }

    /*
      ----------------------Center
       */


    private val midiFileLabel = JLabel(getMessages("Opened_MIDI_File")).usingGlobalProperties()


    private val midiFilePathTextArea = JTextArea(ConfigItem.MIDIFile.load()).usingGlobalProperties()
    private val midiFilePathPane = JScrollPane(midiFilePathTextArea)

    private val deviceLabel = JLabel(getMessages("Opened_Device")).usingGlobalProperties()


    private val deviceOrSf2PathTextArea = JTextArea(ConfigItem.MIDIOutputDevice.load()).usingGlobalProperties()
    private val deviceOrSf2PathPane = JScrollPane(deviceOrSf2PathTextArea)

    private val statuePanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(midiFileLabel)
        add(midiFilePathPane)
        add(deviceLabel)
        add(deviceOrSf2PathPane)
    }


    private val mainPanel = JPanel(BorderLayout(5, 5)).apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 添加边缘边距
        add(buttons, BorderLayout.WEST)
        add(midiDevicePanel, BorderLayout.SOUTH)
        add(statuePanel, BorderLayout.CENTER)
    }


    init {

        defaultCloseOperation = DISPOSE_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                Gdx.app.exit()
            }

        })
        add(mainPanel)

        pack() // 根据组件首选大小调整窗口
        minimumSize = Dimension(640, 480)
        setLocationRelativeTo(null) // 窗口居中

        isVisible = true
    }

}
