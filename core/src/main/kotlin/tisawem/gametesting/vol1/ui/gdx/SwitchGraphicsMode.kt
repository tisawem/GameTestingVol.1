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

import com.badlogic.gdx.Gdx
import tisawem.gametesting.vol1.config.Config
import tisawem.gametesting.vol1.toolkit.Toolkit
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog

object SwitchGraphicsMode {
    /**
     * Run Gdx.graphics.setWindowedMode() function based on the resolution specified in [Config.WindowedResolution].
     *
     * @return `true` if the windowed mode was successfully set; `false` if an error occurred during the process.
     */
    fun setWindowedModeFromConfigItem(): Boolean=try {
        if (Gdx.graphics==null) {
            throw IllegalStateException("请启动Lwjgl3Application实例后，再调用此函数。")
        }
        val (w,h)= Toolkit.getWindowedResolution()

          Gdx.graphics.setWindowedMode(w,h)
    }catch (e: Throwable){
        ExceptionDialog(
            e, true, """
1、IllegalStateException
    请启动Lwjgl3Application实例后，再调用此函数。

其他错误为未知错误。此函数将返回false。
"""
        )
        false
    }


    fun setFullScreenMode () =    try {
        if (Gdx.graphics==null) throw IllegalStateException("请启动Lwjgl3Application实例后，再调用setFullScreenModeByJFrame()函数。")

        if (!Gdx.graphics.supportsDisplayModeChange()|| Gdx.graphics.displayModes.isNullOrEmpty()||Gdx.graphics.displayMode==null) throw UnsupportedOperationException("不支持切换全屏")

        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)


    }catch (e: Throwable){

        ExceptionDialog(e,true,"""
1、IllegalStateException
    请启动Lwjgl3Application实例后，再调用此函数。

2、UnsupportedOperationException
    不支持切换全屏。

其他错误为未知错误。此函数将返回false。
""")

        false
    }


    fun switchFullScreenOrWindowed(){
        if (Gdx.graphics==null) {
            ExceptionDialog(IllegalStateException(),true,"请启动Lwjgl3Application实例后，再调用switchScreenMode()函数。")
            return
        }
        if (Gdx.graphics.isFullscreen){
            setWindowedModeFromConfigItem()

        }else{
            setFullScreenMode()
        }
    }


}
