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

package tisawem.gametesting.vol1.gdx

import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame
import tisawem.gametesting.vol1.Bridge
import tisawem.gametesting.vol1.gdx.screen.GeneralScreen

/**
 * @param bridge 提供一个实现类，以便传入画面要演奏的轨道，获取时间进度等...
 * @param scriptInCreateFunction 任何需要在create函数里面要执行的代码，比如切换演奏界面
 */
class Game  (val bridge: Bridge, private val scriptInCreateFunction:((Game)-> Unit)?) : KtxGame<GeneralScreen>() {



    override fun create() {

        scriptInCreateFunction?.let { it(this) }
    }

}


/*
自从桌面端代码和核心代码分离后，这个拓展函数不再使用

/**
 * Restarts the current game instance by disposing of the existing resources, clearing all registered screens,
 * and reinitializing the game. This method uses reflection to access and clear the protected `screens` property
 * of the `KtxGame` class, ensuring a clean state before invoking the `create` method to set up the game again.
 *
 * If an exception occurs during the restart process, an `ExceptionDialog` is displayed to handle the error
 * gracefully. The dialog provides options to exit the application.
 */

fun <ScreenType : Screen> KtxGame<ScreenType>.restartGameInstance()  {

        dispose()

        // Get the KtxGame class
        val gameClass = KtxGame::class

        // Find the 'screens' property using reflection
        val screensProperty = gameClass.memberProperties.find { it.name == "screens" }
            ?: throw IllegalStateException("Could not find 'screens' property in ktx.app.KtxGame class")

        // Make the property accessible (since it's protected)
        screensProperty.isAccessible = true

        // Get the screens map from the game instance
        @Suppress("UNCHECKED_CAST")
        val screensMap = screensProperty.get(this) as ObjectMap<Class<out ScreenType>, ScreenType>


        // Clear all screens from the map
        screensMap.clear()


        create()


}
*/

