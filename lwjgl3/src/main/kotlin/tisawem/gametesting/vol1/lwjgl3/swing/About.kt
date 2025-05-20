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

import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.lwjgl3.i18n.Messages.getMessages
import java.awt.Dimension
import java.awt.Font
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea

class About(frame: JFrame) : JDialog(frame, getMessages("About"),true)  {

private val textArea= JTextArea(  About::class.java
    .classLoader
    .getResourceAsStream("about/about_${CoreConfig.Language.load()}.txt")
    ?.bufferedReader( )
    ?.use { it.readText() }
    .orEmpty()   ).apply {
    isEditable=false
    font = Font(null, Font.PLAIN, 16)


}

    private val textScrollPane= JScrollPane(textArea)


init {
add(textScrollPane)
pack()

    minimumSize= frame.minimumSize
    size=minimumSize
    setLocationRelativeTo(frame)

    isVisible=true


}

}
