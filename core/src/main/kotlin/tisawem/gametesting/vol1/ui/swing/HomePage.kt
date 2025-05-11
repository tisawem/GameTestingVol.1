package tisawem.gametesting.vol1.ui.swing

import arrow.core.Either
import com.badlogic.gdx.Gdx
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.config.Config
import tisawem.gametesting.vol1.file.ExtensionFilter
import tisawem.gametesting.vol1.file.FileCheckingMethod
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.midi.synth.MidiDeviceManager
import tisawem.gametesting.vol1.ui.swing.FileLoader.loopingAskUserForFileOrAbandon
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*

class HomePage(val game: KtxGame<KtxScreen>) : JFrame("GameTestingVol.1 ${getMessages("HomePage")}") {
    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        defaultCloseOperation = DO_NOTHING_ON_CLOSE
addWindowListener(object : WindowAdapter() {
    override fun windowClosing(e: WindowEvent?) {
         Gdx.app.exit()
    }
})
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

      }


    /*
    ----------------------West
     */


    private val play = JButton(getMessages("Play")).apply {
        usingGlobalProperties()
        toolTipText = getMessages("Play_Tips")
    }

    private val settings = JButton(getMessages("Settings")).apply {
        usingGlobalProperties()
        addActionListener {
            Settings(this@HomePage,game)
        }
    }

    private val openMidiFile = JButton(getMessages("Open_MIDI_File")).apply {
        usingGlobalProperties()
        toolTipText = getMessages("Open_MIDI_File_Tips")
        addActionListener {
           loopingAskUserForFileOrAbandon({FileLoader.loadingFileFromJFileChooser(
               Config.MIDIFile.load(),
               ExtensionFilter.MIDIFile.filter,
               FileCheckingMethod.MIDIFile.method
           )})?.let { Config.MIDIFile.write(it.canonicalPath)
           midiFilePathTextArea.text=it.canonicalPath

           }

        }
    }
    private val openSoundFont = JButton(getMessages("Open_SoundFont")).apply {
        usingGlobalProperties()
        toolTipText = getMessages("Open_SoundFont_Tips")
        addActionListener {
            loopingAskUserForFileOrAbandon({FileLoader.loadingFileFromJFileChooser(
                Config.MIDIOutputDevice.load(),
                ExtensionFilter.SoundFont.filter,
                FileCheckingMethod.SoundFont.method
            )})?.let { Config.MIDIOutputDevice.write(it.canonicalPath)
                deviceOrSf2PathTextArea.text=it.canonicalPath

            }

        }
    }
    private val exit = JButton(getMessages("Exit")).apply {
        usingGlobalProperties()
        addActionListener {
            this@HomePage.dispose()
            Gdx.app.exit()
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
        toolTipText=getMessages("Output_MIDI_Device_Tips")+"  Microsoft MIDI Mapper  Microsoft GS Wavetable Synth"

       addActionListener {
          selectedItem?.let {
              Config.MIDIOutputDevice.write(it.toString() )
              deviceOrSf2PathTextArea.text=it.toString()
          }
       }


    }

    private val midiDevicePanel = JPanel(GridLayout(2, 1, 5, 5)).apply {
        add(midiDeviceLabel)
        add(midiDevice)
    }

    /*
      ----------------------Center
       */


    private val midiFileLabel = JLabel(getMessages("Will_Used_MIDI_File")).usingGlobalProperties()


    private val midiFilePathTextArea = JTextArea(Config.MIDIFile.load()).apply {
        usingGlobalProperties()
        toolTipText=getMessages("Will_Used_MIDI_File_Tips")
    }
    private val midiFilePathPane = JScrollPane(midiFilePathTextArea)

    private val deviceLabel = JLabel(getMessages("Will_Used_Device")).usingGlobalProperties()


    private val deviceOrSf2PathTextArea = JTextArea(Config.MIDIOutputDevice.load()).apply {
        usingGlobalProperties()
        toolTipText=getMessages("Will_Used_Device_Tips")
    }
    private val deviceOrSf2PathPane = JScrollPane(deviceOrSf2PathTextArea)

    private val statuePanel = JPanel(/*没把布局放在这里的原因是：BoxLayout需要传入statuePanel的实例*/).apply {
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

        add(mainPanel)

        pack() // 根据组件首选大小调整窗口
        minimumSize = Dimension(640, 480)
        setLocationRelativeTo(null) // 窗口居中

        isVisible = true
    }

}
