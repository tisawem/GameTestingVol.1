package tisawem.gametesting.vol1.gdx.screen

import com.badlogic.gdx.Gdx
import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.gdx.Game
import tisawem.gametesting.vol1.gdx.GridLayout
import tisawem.gametesting.vol1.gdx.musician.AllocateMusicianFunctions
import tisawem.gametesting.vol1.midi.InstrumentStandard
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * 本类基于Duration，而非Tick长度
 */
class Perform(private val gameInstance: Game) : GeneralScreen(gameInstance) {


    /**
     * 往舞台放置的乐手
     */
    private val gridLayout = GridLayout().apply {
        setSize(viewport.worldWidth,viewport.worldHeight)

    }

    /**
     * 提前演奏的时间
     */
    private val advancedTime =
        CoreConfig.ScreenAdvancedTime.load().toIntOrNull()?.toDuration(DurationUnit.MILLISECONDS) ?: Duration.ZERO

    /**
     * 获得当前播放进度
     */
    private fun getCurrentPosition() = (gameInstance.bridge.getPosition() ?: Duration.ZERO) + advancedTime

    /**
     * 序列总时长
     */
    private val sequenceLength = gameInstance.bridge.timedBaseSequence.duration + advancedTime


    /**
     * 准备播放标志，由[tisawem.gametesting.vol1.Bridge.play]传入的两个函数修改，也就是平台端的播放器更改它的状态
     */
    private var ready=false


    init {
        gameInstance.bridge.score.filter { it.arcs.isEmpty() }
            .forEach {
                val instrumentChange = it.instrumentChanges.firstOrNull()
                    ?: InstrumentStandard(it.arcs.first().channel).defaultInstrumentChange//保证至少有1个乐器变更事件


                AllocateMusicianFunctions.RANDOM.allocate(instrumentChange)
                    ?.invoke(gameInstance.bridge.timedBaseSequence, it) { getCurrentPosition() }?.let { musician ->
                        gridLayout.addActor(musician.getActor())
                    }
            }

        stage.addActor(gridLayout)
gameInstance.bridge.create  ({ready=true}){ready=false}
gameInstance.bridge.play()

    }


    override fun render(delta: Float) {
        if (ready ) {
            super.render(delta)
        }else{
            Gdx.app.exit()

        }


    }

    override fun dispose() {
        super.dispose()

    }
}






