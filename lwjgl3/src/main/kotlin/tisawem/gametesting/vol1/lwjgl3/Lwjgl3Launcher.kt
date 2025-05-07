@file:JvmName("Lwjgl3Launcher")

package tisawem.gametesting.vol1.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import tisawem.gametesting.vol1.ui.gdx.Game

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    try {
        Lwjgl3Application(Game(), Lwjgl3ApplicationConfiguration().apply {
            setTitle("GameTestingVol.1")
            // Vsync limits the frames per second to what your hardware can display, and helps eliminate
            // screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
            useVsync(true)
            // Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
            // refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
            setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1)
            // If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
            // useful for testing performance, but can also be very stressful to some hardware.
            // You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

            // You can change these files; they are in lwjgl3/src/main/resources/ .
            // They can also be loaded from the root of assets/ .
            setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
        })
    }catch (e: Throwable){
        ExceptionDialog(e,false,"""
未知错误

    正常情况下，程序不应该显示该对话框。
    如果出现了意外，没有处理机制。
    程序将被迫中止。

Unknown Error

    Normally, the program should not display this dialog box.
    If an unexpected situation occurs and there is no handling mechanism,
    The program will be forced to terminate.

        """.trimIndent())
    }
}
