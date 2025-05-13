package tisawem.gametesting.vol1.ui.gdx.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

class Perform(val game: KtxGame<KtxScreen>): KtxScreen {
val batch=SpriteBatch()
    val viewport= FitViewport(3840f,2160f)


    val stage= Stage(viewport,batch)


    override fun dispose() {
        stage.disposeSafely()
    }

}
