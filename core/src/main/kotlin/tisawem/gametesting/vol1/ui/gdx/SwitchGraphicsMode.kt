package tisawem.gametesting.vol1.ui.gdx

import com.badlogic.gdx.Gdx
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog

object SwitchGraphicsMode {
    /**
     * Run Gdx.graphics.setWindowedMode() function based on the resolution specified in [ConfigItem.WindowedResolution].
     *
     * @return `true` if the windowed mode was successfully set; `false` if an error occurred during the process.
     */
    private fun setWindowedModeFromConfigItem(): Boolean=try {
        if (Gdx.graphics==null) {
            throw IllegalStateException("请启动Lwjgl3Application实例后，再调用此函数。")
        }
        val (x,y)=ConfigItem.WindowedResolution.load().split('_', ignoreCase = false, limit = 2).map { it.toInt().takeIf { number -> number>0 }?:throw NumberFormatException("范围不对") }
        Gdx.graphics.setWindowedMode(x,y)
    }catch (e: Throwable){
        ExceptionDialog(
            e, true, """
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties的WindowedResolution设置项, 文本格式，或者范围不对：
        当前设置项的值为：${ConfigItem.WindowedResolution.load()}

    正确格式为 <横向分辨率>_<纵向分辨率> ，值均为正整数。

2、NoSuchElementException
    请检查 config.properties文件 是否缺 WindowedResolution 设置项

3、IllegalStateException
    请启动Lwjgl3Application实例后，再调用此函数。

其他错误为未知错误。
"""
        )
        false
    }


    private fun setFullScreenMode () =    try {
        if (Gdx.graphics==null) throw IllegalStateException("请启动Lwjgl3Application实例后，再调用setFullScreenModeByJFrame()函数。")

        if (!Gdx.graphics.supportsDisplayModeChange()|| Gdx.graphics.displayModes.isNullOrEmpty()||Gdx.graphics.displayMode==null) throw UnsupportedOperationException("不支持切换全屏")

        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)


    }catch (e: Throwable){

        ExceptionDialog(e,true,"""
1、IllegalStateException
    请启动Lwjgl3Application实例后，再调用此函数。

2、UnsupportedOperationException
    不支持切换全屏。

其他错误为未知错误。
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
