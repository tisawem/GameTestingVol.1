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
enum class ExtensionFilter(val filter:()-> FileFilter) {
    MIDIFile("MIDI_File","mid"),
    SoundFont("SoundFont_File","sf2"),
    Image("Image_File","jpg","png","bmp"),
    ;
    constructor(messages:String,vararg extensions:String  ):this(
        {
            FileNameExtensionFilter(
                Messages.getMessages(messages),
                *extensions
            )
        }
    )
}
