package tisawem.gametesting.vol1.gdx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import tisawem.gametesting.vol1.config.CoreConfig
import kotlin.math.min

/**
 * 减少样板代码，统一外观的抽象屏幕类
 */
abstract  class GeneralScreen (val game: KtxGame<GeneralScreen>): KtxScreen{
   private val batch= SpriteBatch()
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
                setScale(min(viewport.worldWidth/backgroundTexture.width.toFloat(),viewport.worldHeight/backgroundTexture.height.toFloat()))
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
