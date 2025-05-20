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

package tisawem.gametesting.vol1.gdx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import tisawem.gametesting.vol1.config.CoreConfig
import kotlin.math.min

/**
 * 减少样板代码，统一外观的抽象屏幕类
 */
abstract  class GeneralScreen (): KtxScreen{
   val batch= SpriteBatch()
    val viewport= FitViewport(3840f,2160f)


    val backgroundTexture  = try {
        Texture( Gdx.files.internal(CoreConfig.PerformBackgroundImage.load()))
    }catch (_: Throwable){
        try {
            Texture( Gdx.files.external(CoreConfig.PerformBackgroundImage.load()))
        }catch (_: Throwable){
            null
        }

    }
    val stage= Stage(viewport,batch).apply {
        Gdx.input.inputProcessor=this
        if (backgroundTexture != null) {
            addActor(Image(backgroundTexture).apply {
                val scale = min(viewport.worldWidth/backgroundTexture.width.toFloat(), viewport.worldHeight/backgroundTexture.height.toFloat())
                setScale(scale)
// 考虑缩放后的尺寸进行居中
                setPosition(
                    (viewport.worldWidth - backgroundTexture.width * scale)/2,
                    (viewport.worldHeight - backgroundTexture.height * scale)/2
                )
            })
        }
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.TAN)
        batch.projectionMatrix = viewport.camera.combined



        stage.act(delta)
        stage.draw()



    }



    override fun resize(width: Int, height: Int) {
        viewport.update(width,height,true)
    }


    override fun dispose() {
stage.disposeSafely()
    }
}
