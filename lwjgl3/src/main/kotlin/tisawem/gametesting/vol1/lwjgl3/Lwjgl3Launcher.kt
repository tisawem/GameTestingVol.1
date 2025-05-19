@file:JvmName("Lwjgl3Launcher")

package tisawem.gametesting.vol1.lwjgl3

import tisawem.gametesting.vol1.config.CoreConfigOperation
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfigOperation
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import tisawem.gametesting.vol1.lwjgl3.swing.HomePage
import java.io.FileNotFoundException
import java.util.Properties

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return

try {
    /*
 * 初始化各配置读写类的Properties实例
 */
    val desktopInputStream=DesktopConfig::class.java.classLoader.getResourceAsStream("DesktopConfig.properties")?: ExceptionDialog(
        FileNotFoundException(),false,"DesktopConfig.properties 文件没找到。").onExit()


    DesktopConfigOperation.configProperties= Properties().apply {
        load(desktopInputStream)
        desktopInputStream.close()
    }

    val coreInputStream=DesktopConfig::class.java.classLoader.getResourceAsStream("CoreConfig.properties")?: ExceptionDialog(
        FileNotFoundException(),false,"CoreConfig.properties 文件没找到。").onExit()

    CoreConfigOperation.configProperties= Properties().apply {
        load(coreInputStream)
        coreInputStream.close()
    }
    HomePage()
}catch (e: Throwable){
    ExceptionDialog(e,false,"程序发生了意外的错误")
}
}
