package tisawem.gametesting.vol1.ui.swing

import com.badlogic.gdx.Gdx
import com.ibm.icu.util.ULocale
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.i18n.Messages
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.ui.gdx.Game
import tisawem.gametesting.vol1.ui.gdx.screen.HomePageIDLE
import java.awt.Dimension
import java.awt.Font
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.UIManager

class Settings(frame: JFrame,val game: KtxGame<KtxScreen>): JDialog(frame,getMessages("Settings"),true) {

    init {
        UIManager.setLookAndFeel(  UIManager.getSystemLookAndFeelClassName())
        defaultCloseOperation = DISPOSE_ON_CLOSE
    }





private val backButton= JButton(getMessages("Back")).apply {
    font= Font(null, Font.BOLD,24)
    addActionListener {
dispose()
    }

}


    private val changeLanguageLabel= JLabel(getMessages("Change_Language"))



    private val languageBox= JComboBox(Messages.SupportedLanguage.entries. toTypedArray()).apply {
        font= Font(null,Font.PLAIN,28)
        addActionListener {
            selectedItem?.let {
                ConfigItem.Language.write((it as Messages.SupportedLanguage).locale.name)
                dispose()
                frame.dispose()
                HomePageIDLE.languageChanged=true



            }
        }
    }




    init {
        add(languageBox)
        pack() // 根据组件首选大小调整窗口
        minimumSize = Dimension(480, 480)
         setLocationRelativeTo(frame) // 窗口居中
        isVisible=true
    }
}
