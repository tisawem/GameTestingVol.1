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
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.centerPosition
import ktx.assets.disposeSafely
import tisawem.gametesting.vol1.config.CoreConfig
import tisawem.gametesting.vol1.gdx.Game
import tisawem.gametesting.vol1.gdx.OverlayInstrumentLayout
import tisawem.gametesting.vol1.gdx.musician.AllocateMusicianFunctions
import tisawem.gametesting.vol1.gdx.musician.Musician
import tisawem.gametesting.vol1.midi.InstrumentStandard
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * 本类基于Duration，而非Tick长度
 */
class Perform(private val gameInstance: Game) : GeneralScreen() {

    /**
     * 布局
     */
    private val generalLayout = OverlayInstrumentLayout()

    private val percussionLayout= OverlayInstrumentLayout()

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
     * 序列总时长，不含ScreenAdvancedTime
     *
     * 不保证与MIDI播放器提供的序列总时长一致
     *
     * 单位：秒
     */
    private val sequenceLength = gameInstance.bridge.timedBaseSequence.duration.toDouble(DurationUnit.SECONDS).toFloat()

    /**
     * 内部计数器，以免播放器卡死永远不会退出
     *
     * 单位：秒
     */
    private var internalTimer=0f



    /**
     * 准备播放标志，由[tisawem.gametesting.vol1.Bridge.play]传入的两个函数修改，也就是平台端的播放器更改它的状态
     */
    private var ready=false

    /**
     * 已经播放标志
     *
     * 只有played为true，ready是false时，程序才会关闭
     */
    private var played=false

    private val generalActors= ArrayDeque<Musician>()
    private val percussionActors= ArrayDeque<Musician>()

    init {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.ESCAPE) {
Gdx.app.exit()

                }
                return true
            }
        }



        gameInstance.bridge.score.filter { it.arcs.isNotEmpty() }.forEach { score ->
                val instrumentChange = score.instrumentChanges.firstOrNull()
                    ?: InstrumentStandard(score.arcs.first().channel).defaultInstrumentChange//保证至少有1个乐器变更事件

                //分配Actor
                AllocateMusicianFunctions.RANDOM.allocate(instrumentChange)?.let { musician->
                   when( score.arcs.first().channel){
                       InstrumentStandard.PERCUSSION_CHANNEL->percussionActors.add(musician(gameInstance.bridge.timedBaseSequence,score) { getCurrentPosition() })
                   else -> generalActors.add(musician(gameInstance.bridge.timedBaseSequence,score) { getCurrentPosition() } )

                   }

                }



            }

        generalLayout.apply {
            setMelodicInstruments(generalActors.map { it.getActor() })

            this@Perform.stage .addActor(this)
            setFillParent(true)
        }

        percussionLayout.apply {
            setMelodicInstruments(percussionActors.map { it.getActor() })
             setScale(0.5f)
            centerPosition(viewport.worldWidth/2,0f)
            this@Perform.stage .addActor(this)
            setFillParent(true)
        }



gameInstance.bridge.create  ({ready=true}){ready=false;played=true }
gameInstance.bridge.play()

    }


    override fun render(delta: Float) {
        if (ready ) {
            super.render(delta)

            internalTimer+=delta
        }else if (played){
            Gdx.app.exit()

        }

        if (internalTimer>sequenceLength) Gdx.app.exit()

    }

    override fun dispose() {
        super.dispose()
        generalActors.forEach { it.disposeSafely()}
        percussionActors.forEach { it.disposeSafely() }
        gameInstance.bridge.stop()
    }
}






