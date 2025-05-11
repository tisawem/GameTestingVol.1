package tisawem.gametesting.vol1.file

import tisawem.gametesting.vol1.i18n.Messages
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 提供JFileChooser可用的FileFilter的枚举类
 *
 *
 * @constructor messages: 从messages.properties获取的字符串
 *
 * extensions: 文件拓展名
 *
 * @see  tisawem.gametesting.vol1.ui.swing.FileLoader.loadingFileFromJFileChooser
 */
enum class ExtensionFilter(val filter: FileFilter) {
    MIDIFile("MIDI_File","mid"),
    SoundFont("SoundFont_File","sf2"),
    Image("Image_File","jpg","png","bmp"),
    ;
    constructor(messages:String,vararg extensions:String  ):this(
        FileNameExtensionFilter(
            Messages.getMessages(messages),
            *extensions
        )
    )
}
