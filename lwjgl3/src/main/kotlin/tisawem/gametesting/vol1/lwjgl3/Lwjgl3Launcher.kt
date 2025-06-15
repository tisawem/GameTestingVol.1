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

@file:JvmName("Lwjgl3Launcher")

package tisawem.gametesting.vol1.lwjgl3

import tisawem.gametesting.vol1.config.CoreConfigOperation
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfigOperation
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import tisawem.gametesting.vol1.lwjgl3.swing.HomePage
import java.io.FileNotFoundException
import java.util.*

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return




    try {
        /*
     * 初始化各配置读写类的Properties实例
     */


        DesktopConfigOperation.configProperties = getProperties("DesktopConfig.properties")
        CoreConfigOperation.configProperties = getProperties("CoreConfig.properties")
        HomePage()
    } catch (e: Throwable) {
        ExceptionDialog(e, false, "程序发生了意外的错误")
    }
}

fun getProperties(resourcePath: String, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): Properties {
    val inputStream = classLoader.getResourceAsStream(resourcePath) ?: ExceptionDialog(
        FileNotFoundException(), false, "$resourcePath 文件没找到。"
    ).onExit()

    return Properties().apply {
        inputStream.use { load(it) }
    }
}
