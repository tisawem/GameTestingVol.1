package tisawem.gametesting.vol1.gdx.screen

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
import tisawem.gametesting.vol1.config.CoreConfig

/**
 * 减少样板代码，统一外观的抽象屏幕类
 */
  class PerformScreen (val game: KtxGame<KtxScreen>): KtxScreen{
   private val batch= SpriteBatch()
    val viewport= FitViewport(3840f,2160f)


    val backgroundTexture  = Texture(try {
        Gdx.files.internal(CoreConfig.PerformBackgroundImage.load())
    }catch (_: Throwable){
        Gdx.files.external(CoreConfig.PerformBackgroundImage.load())
    })
    var background= Sprite(backgroundTexture)


    val stage= Stage(viewport,batch).apply {
        Gdx.input.inputProcessor=this
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.TAN)
        batch.projectionMatrix = viewport.camera.combined


        batch.begin()
        background.draw(batch)
        batch.end()
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
