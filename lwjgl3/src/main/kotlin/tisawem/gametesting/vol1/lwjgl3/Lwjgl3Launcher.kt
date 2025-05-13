@file:JvmName("Lwjgl3Launcher")

package tisawem.gametesting.vol1.lwjgl3

import tisawem.gametesting.vol1.lwjgl3.swing.HomePage

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    HomePage()
}
