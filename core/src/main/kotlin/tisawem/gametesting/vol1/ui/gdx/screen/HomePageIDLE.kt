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



  companion object{
      /**
       * 当修改语言后，将重新创建HomePageIDLE实例，重新渲染字体
       *
       * 给[tisawem.gametesting.vol1.ui.swing.Settings]用的，它设置语言后，将更改这个值为true
       */
      var languageChanged= false
  }

    /**
     * 当GDX窗口被叉掉关闭时，也顺便关闭homePage实例
     */
    lateinit var homePage: HomePage

    override fun show() {

         Gdx.app.postRunnable { homePage=HomePage(game) }

    }

    override fun render(delta: Float) {
if(languageChanged ){
    languageChanged=false
    game.removeScreen<HomePageIDLE>()
    game.addScreen<HomePageIDLE>(HomePageIDLE(game))
    game.setScreen<HomePageIDLE>()
}
    }

    override fun dispose() {
       homePage.dispose()
    }

}
