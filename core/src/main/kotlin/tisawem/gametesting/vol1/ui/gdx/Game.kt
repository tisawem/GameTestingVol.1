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

package tisawem.gametesting.vol1.ui.gdx

import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.ObjectMap
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.ui.gdx.screen.HomePageIDLE
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


/**
 * KtxGame，主要使用它的切换屏幕功能，以及不用在create方法里面初始化资源
 *
 * 通常情况下，各个屏幕只有[Game]实例是互相传来传去
 *
 * 配置读写在[tisawem.gametesting.vol1.config.Config]操作
 *
 * 通常，各个Screen在切换屏幕时，会销毁自己的实例，这很重要
 */
class Game : KtxGame<KtxScreen>() {



    override fun create() {
        SwitchGraphicsMode.setWindowedModeFromConfigItem()

        addScreen<HomePageIDLE>(HomePageIDLE(this))
        setScreen<HomePageIDLE>()
    }





}

/**
 * [tisawem.gametesting.vol1.ui.swing.Settings]专用函数，专门给[Game]调用的扩展函数
 *
 * 唯一调用它的情况，就是切换语言。
 *
 * 就是所有的swing窗口关了，把所有的[ScreenType]清了
 *
 * 然后调用[KtxGame.create]函数，重新启动[HomePageIDLE]，再启动[tisawem.gametesting.vol1.ui.swing.HomePage]窗口。
 *
 * Restarts the current game instance by disposing of the existing resources, clearing all registered screens,
 * and reinitializing the game. This method uses reflection to access and clear the protected `screens` property
 * of the `KtxGame` class, ensuring a clean state before invoking the `create` method to set up the game again.
 *
 * If an exception occurs during the restart process, an `ExceptionDialog` is displayed to handle the error
 * gracefully. The dialog provides options to exit the application.
 */
fun <ScreenType : Screen> KtxGame<ScreenType>.restartGameInstance()  {
    try {
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

    } catch (e: Exception) {
        ExceptionDialog(e,false,"切换语言时，试图重启屏幕时，出现了问题。")

    }
}

