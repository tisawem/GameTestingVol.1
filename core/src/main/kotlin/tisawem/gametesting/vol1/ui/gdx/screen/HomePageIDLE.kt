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

package tisawem.gametesting.vol1.ui.gdx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import tisawem.gametesting.vol1.config.Config
import tisawem.gametesting.vol1.ui.gdx.restartGameInstance
import tisawem.gametesting.vol1.ui.swing.HomePage
import kotlin.math.min

class HomePageIDLE(val game: KtxGame<KtxScreen>) : KtxScreen{
    val batch= SpriteBatch()
    val viewport= FitViewport(3840f,2160f)



    /*
     * 各种Changed的字段，供[tisawem.gametesting.vol1.ui.swing.Settings]修改
     *
     * 非GDX线程内的代码，不便执行GDX的代码，所以通过更改标志的方式，通知并响应更改。
     */

      /**
       * 当修改语言后，将重新创建HomePageIDLE实例，重新渲染字体
       *
       * 给[tisawem.gametesting.vol1.ui.swing.Settings]用的，它设置语言后，将更改这个值为true
       */
      var languageChanged= false

        /**
         * 设置为true，以便初始化时，就加载背景图片
         *
         * 优先尝试加载为内部路径，再尝试加载为外部路径
         */
        var backgroundChanged=true



    /**
     * 当GDX窗口被叉掉关闭时，也顺便关闭homePage实例
     */
    lateinit var homePage: HomePage

    override fun show() {

         Gdx.app.postRunnable { homePage=HomePage(game) }

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width,height,true)
    }


    //上面是样板代码，下面是资源


    var backgroundTexture:Texture?=null
    var background:Sprite?=null



    private val stage= Stage(viewport,batch).apply {
        Gdx.input.inputProcessor=this



    }






    override fun render(delta: Float) {
        ScreenUtils.clear(Color.TAN)
checkStatueChange()


        batch.projectionMatrix = viewport.camera.combined
        batch.begin()

        background?.draw(batch)



        batch.end()

        stage.act(delta)
        stage.draw()

    }


    private fun checkStatueChange(){
        if (languageChanged){
            languageChanged=false
            game.restartGameInstance()
        }

        if (backgroundChanged){
            backgroundChanged=false
            try {
               backgroundTexture=Texture(Gdx.files.internal(Config.PerformBackgroundImage.load())!!)

            }catch (_: Throwable){
                try {
                    backgroundTexture=Texture(Gdx.files.external(Config.PerformBackgroundImage.load()))

                }catch (_: Throwable){


                }
            }

            backgroundTexture?.let { texture ->
                // 计算缩放比例
                val scaleX = viewport.worldWidth / texture.width.toFloat()
                val scaleY = viewport.worldHeight / texture.height.toFloat()
                val scale = min(scaleX, scaleY)

                background = Sprite(texture)

                    .apply {

                    // 设置缩放
                    setScale(scale)

                    // 先将原点设为中心
                    setOriginCenter()

                    // 将sprite的中心放到viewport的中心
                    setCenter(viewport.worldWidth / 2f, viewport.worldHeight / 2f)
                }
            }


        }

    }


    override fun dispose() {
       homePage.dispose()
        stage.disposeSafely()//不用关闭batch，stage已经把它关了
        backgroundTexture?.disposeSafely()

    }

}
