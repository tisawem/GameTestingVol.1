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
