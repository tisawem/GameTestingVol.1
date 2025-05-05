package tisawem.gametesting.vol1.ui.gdx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.ui.swing.HomePage

class HomePageIDLE(val game: KtxGame<KtxScreen>) : KtxScreen{
    val batch= SpriteBatch()
    val viewport= FitViewport(3840f,2160f)

    override fun show() {
         Gdx.app.postRunnable { HomePage(game) }
    }

    override fun render(delta: Float) {

    }

    override fun dispose() {

    }

}
